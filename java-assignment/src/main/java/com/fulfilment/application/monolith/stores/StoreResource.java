package com.fulfilment.application.monolith.stores;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.arc.Arc;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import org.jboss.logging.Logger;

import static jakarta.transaction.Status.STATUS_COMMITTED;

@Path("stores")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class StoreResource {

  @Inject LegacyStoreManagerGateway legacyStoreManagerGateway;
  @Inject
  TransactionManager transactionManager;

  private static final Logger LOGGER = Logger.getLogger(StoreResource.class.getName());

  @GET
  public List<Store> get() {
    return Store.listAll(Sort.by("name"));
  }

  @GET
  @Path("{id}")
  public Store getSingle(Long id) {
    Store entity = Store.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }
    return entity;
  }

  @POST
  @Transactional
  public Response create(Store store) {
    if (store.id != null) {
      throw new WebApplicationException("Id was invalidly set on request.", 422);
    }

    store.persist();

    //legacyStoreManagerGateway.createStoreOnLegacySystem(store);
    try {
      // Register the synchronization with the JTA transaction manager
      transactionManager.getTransaction().registerSynchronization(new Synchronization() {
        @Override
        public void beforeCompletion() {
          // No action needed before commit
        }

        @Override
        public void afterCompletion(int status) {
          // status 3 is STATUS_COMMITTED
          if (status == STATUS_COMMITTED) {
            legacyStoreManagerGateway.createStoreOnLegacySystem(store);
          }
        }
      });
    } catch (Exception e) {
      // If we can't register the sync, we should probably fail the request
      throw new WebApplicationException("Could not schedule legacy synchronization", 500);
    }

    return Response.ok(store).status(201).build();
  }

  @PUT
  @Path("{id}")
  @Transactional
  public Store update(Long id, Store updatedStore) {
    if (updatedStore.name == null) {
      throw new WebApplicationException("Store Name was not set on request.", 422);
    }

    Store entity = Store.findById(id);

    if (entity == null) {
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }

    entity.name = updatedStore.name;
    entity.quantityProductsInStock = updatedStore.quantityProductsInStock;

    //legacyStoreManagerGateway.updateStoreOnLegacySystem(updatedStore);
    try {
      // Register the synchronization to fire ONLY after a successful DB commit
      transactionManager.getTransaction().registerSynchronization(new Synchronization() {
        @Override
        public void beforeCompletion() {}

        @Override
        public void afterCompletion(int status) {
          if (status == jakarta.transaction.Status.STATUS_COMMITTED) {
            // We send the updated entity to the legacy system
            legacyStoreManagerGateway.updateStoreOnLegacySystem(entity);
          }
        }
      });
    } catch (Exception e) {
      throw new WebApplicationException("Could not schedule legacy synchronization", 500);
    }
    return entity;
  }

  @PATCH
  @Path("{id}")
  @Transactional
  public Store patch(Long id, Store updatedStore) {
    if (updatedStore.name == null) {
      throw new WebApplicationException("Store Name was not set on request.", 422);
    }

    Store entity = Store.findById(id);

    if (entity == null) {
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }

    if (entity.name != null) {
      entity.name = updatedStore.name;
    }

    if (entity.quantityProductsInStock != 0) {
      entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
    }

    // legacyStoreManagerGateway.updateStoreOnLegacySystem(updatedStore);
    try {
      transactionManager.getTransaction().registerSynchronization(new Synchronization() {
        @Override
        public void beforeCompletion() {}

        @Override
        public void afterCompletion(int status) {
          if (status == jakarta.transaction.Status.STATUS_COMMITTED) {
            // Confirming the change to the legacy system only after DB commit
            legacyStoreManagerGateway.updateStoreOnLegacySystem(entity);
          }
        }
      });
    } catch (Exception e) {
      throw new WebApplicationException("Could not schedule legacy synchronization", 500);
    }
    return entity;
  }

  @DELETE
  @Path("{id}")
  @Transactional
  public Response delete(Long id) {
    Store entity = Store.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }
    entity.delete();
    return Response.status(204).build();
  }

  @Provider
  public static class ErrorMapper implements ExceptionMapper<Exception> {

    @Inject ObjectMapper objectMapper;

    @Override
    public Response toResponse(Exception exception) {
      LOGGER.error("Failed to handle request", exception);

      int code = 500;
      if (exception instanceof WebApplicationException) {
        code = ((WebApplicationException) exception).getResponse().getStatus();
      }

      ObjectNode exceptionJson = objectMapper.createObjectNode();
      exceptionJson.put("exceptionType", exception.getClass().getName());
      exceptionJson.put("code", code);

      if (exception.getMessage() != null) {
        exceptionJson.put("error", exception.getMessage());
      }

      return Response.status(code).entity(exceptionJson).build();
    }
  }
}
