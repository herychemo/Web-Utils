package com.grayraccoon.webutils.services;

import com.grayraccoon.webutils.errors.ApiValidationError;
import com.grayraccoon.webutils.exceptions.CustomApiException;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Set;

/**
 * This service will perform and collect all possible errors on an entity before saving it into db.
 * The purpose of this service is avoid duplicate code on Service Layer classes for different entities.
 *
 * @author Heriberto Reyes Esparza <hery.chemo@gmail.com>
 */
public interface CommonEntityOperationsService {

    /**
     * Validates entity using {@link CustomValidatorService} and then saves it.
     * If there is any validation error, it will throw a {@link CustomApiException},
     * otherwise It will Save entity using provided {@link CrudRepository}.
     *
     * @param repository Repository where to store entity after successful validation.
     * @param entity Entity to validate.
     * @param <T> Entity to validate Type class.
     * @return result from {@link CrudRepository#save(Object)} after successful validation.
     * @throws CustomApiException If there is any validation error.
     */
    <T extends Serializable> T validateAndSaveEntity(
            CrudRepository<T, ?> repository,
            T entity) throws CustomApiException;

    /**
     * Validates entity using {@link CustomValidatorService} and a {@link ManualValidatorSupplier} and then saves it.
     * If there is any validation error, it will throw a {@link CustomApiException},
     * otherwise It will Save entity using provided {@link CrudRepository}.
     *
     * @param repository Repository where to store entity after successful validation.
     * @param entity Entity to validate.
     * @param manualValidation Callback to perform some manual validations on entity.
     * @param <T> Entity to validate Type class.
     * @return result from {@link CrudRepository#save(Object)} after successful validation.
     * @throws CustomApiException If there is any validation error.
     */
    <T extends Serializable> T validateAndSaveEntity(
            CrudRepository<T, ?> repository,
            T entity,
            ManualValidatorSupplier<T> manualValidation) throws CustomApiException;

    /**
     * Validates entity using {@link CustomValidatorService} and then saves it.
     * If there is any validation error, it will throw a {@link CustomApiException},
     * otherwise It will Save entity using provided {@link CrudRepository}.
     *
     * @param repository Repository where to store entity after successful validation.
     * @param entity Entity to validate.
     * @param postProcessEntity Callback to perform some post processing to entity before saving it.
     * @param <T> Entity to validate Type class.
     * @return result from {@link CrudRepository#save(Object)} after successful validation.
     * @throws CustomApiException CustomApiException If there is any validation error.
     */
    <T extends Serializable> T validateAndSaveEntity(
            CrudRepository<T, ?> repository,
            T entity,
            PostProcessEntity<T> postProcessEntity) throws CustomApiException;

    /**
     * Validates entity using {@link CustomValidatorService} and then saves it.
     * If there is any validation error, it will throw a {@link CustomApiException},
     * otherwise It will Save entity using provided {@link CrudRepository}.
     *
     * @param repository Repository where to store entity after successful validation.
     * @param entity Entity to validate.
     * @param manualValidation Callback to perform some manual validations on entity.
     * @param postProcessEntity Callback to perform some post processing to entity before saving it.
     * @param <T> Entity to validate Type class.
     * @return result from {@link CrudRepository#save(Object)} after successful validation.
     * @throws CustomApiException CustomApiException If there is any validation error.
     */
    <T extends Serializable> T validateAndSaveEntity(
            CrudRepository<T, ?> repository,
            T entity,
            ManualValidatorSupplier<T> manualValidation,
            PostProcessEntity<T> postProcessEntity) throws CustomApiException;

    /**
     * @author Heriberto Reyes Esparza <hery.chemo@gmail.com>
     */
    @FunctionalInterface
    public interface ManualValidatorSupplier<T extends Serializable> {

        /**
         * Callback to apply some manual validations on entity before saving it.
         * eg. Validate that a UserEntity doesn't have an already registered Email in Db.
         *
         * @param entity Entity to validate.
         * @param errors Set of Errors got from Manual Validation.
         */
        void applyManualValidation(T entity, Set<ApiValidationError> errors);
    }

    /**
     * @author Heriberto Reyes Esparza <hery.chemo@gmail.com>
     */
    @FunctionalInterface
    public interface PostProcessEntity<T extends Serializable> {

        /**
         * Callback to apply some post processing to the entity before persist it.
         * This callback will be called after validating entity just before saving it.
         *
         * @param entity Entity to Pre-Process.
         * @return Entity after processing.
         */
        T postProcess(T entity);
    }

}