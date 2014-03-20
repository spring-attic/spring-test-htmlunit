/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package sample.data.mock;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.DomainClassConverter;
import sample.mvc.MessageController;

/**
 * Allows using Mock {@link CrudRepository} instances (i.e. using Mockito) and still support converting from a String to
 * a domain object. This is used internally by Spring on methods like
 * {@link MessageController#view(sample.data.Message)}
 *
 * @author Rob Winch
 *
 * @param <T>
 * @see DomainClassConverter
 */
public class MockConversionService<T extends ConversionService & ConverterRegistry> implements ApplicationContextAware,
		ConditionalGenericConverter {
	@SuppressWarnings("rawtypes")
	private Collection<CrudRepository> repositories;

	private final T conversionService;

	public MockConversionService(T conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Object.class));
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		CrudRepository<Object, Serializable> repo = getRepository(targetType.getType(), sourceType.getType());
		return repo == null ? null : repo.findOne((Serializable) source);
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return getRepository(targetType.getType(), sourceType.getType()) != null;
	}

	public void setApplicationContext(ApplicationContext context) {
		this.repositories = context.getBeansOfType(CrudRepository.class).values();
		this.conversionService.addConverter(this);
	}

	private CrudRepository<Object, Serializable> getRepository(Class<?> domainClass, Class<?> idClass) {
		for (CrudRepository<Object, Serializable> repository : repositories) {
			Class<?>[] typeArgs = GenericTypeResolver.resolveTypeArguments(repository.getClass(), CrudRepository.class);
			if (domainClass.equals(typeArgs[0]) && conversionService.canConvert(typeArgs[1], idClass)) {
				return repository;
			}
		}
		return null;
	}
}
