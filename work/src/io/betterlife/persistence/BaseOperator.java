package io.betterlife.persistence;

import io.betterlife.application.EntityManagerConsumer;
import io.betterlife.domains.BaseObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

/**
 * Author: Lawrence Liu(lawrence@betterlife.io)
 * Date: 10/31/14
 */
public class BaseOperator extends EntityManagerConsumer {
    private static final Logger logger = LogManager.getLogger(BaseOperator.class.getName());
    private static BaseOperator instance = new BaseOperator();

    public static final int UPDATE_OPERA = 1;
    public static final int CREATE_OPERA = 0;
    private EntityManager entityManager;

    public static BaseOperator getInstance() {
        if (instance.entityManager == null || !instance.entityManager.isOpen()) {
            instance.entityManager = instance.newEntityManager();
        }
        return instance;
    }

    private BaseOperator() {
    }

    public <T> T getBaseObjectById(long id, String queryName) {
        Query q = getQuery(queryName);
        q.setParameter("id", id);
        return getSingleResult(q);
    }

    public <T> void save(T obj, int operation) {
        if (obj instanceof BaseObject) {
            final BaseObject bo = (BaseObject) obj;
            final Date date = new Date();
            if (bo.getId() == 0) {
                bo.setCreateDate(date);
                bo.setActive(true);
            }
            bo.setLastModifyDate(date);
            saveBaseObjectWithTransaction(obj, operation);
        }
    }

    public <T> void saveBaseObjectWithTransaction(T obj, int operation) {
        try {
            entityManager.getTransaction().begin();
            if (UPDATE_OPERA == operation) {
                obj = entityManager.merge(obj);
            } else {
                entityManager.persist(obj);
            }
            if (logger.isTraceEnabled()) {
                logger.trace(String.format("Saving Object: [%s]", obj));
            }
            entityManager.flush();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error to save object " + obj);
            logger.error(e);
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } finally {
            closeEntityManager();
        }
    }

    public <T> T getBaseObject(String queryName, Map<String, ?> queryParams) {
        Query q = getQuery(queryName);
        for (Map.Entry<String, ?> entry : queryParams.entrySet()) {
            q.setParameter(entry.getKey(), entry.getValue());
        }
        return getSingleResult(q);
    }

    public <T> List<T> getBaseObjects(String queryName) {
        List<T> result = new ArrayList<>();
        Query q = getQuery(queryName);
        Collection coll = q.getResultList();
        if (coll != null) {
            @SuppressWarnings("unchecked")
            Iterator<T> it = (Iterator<T>) coll.iterator();
            while (it.hasNext()) {
                result.add(it.next());
            }
        }
        return result;
    }

    public Query getQuery(String queryName) {
        return entityManager.createNamedQuery(queryName);
    }

    public <T> T getSingleResult(Query q) {
        @SuppressWarnings("unchecked")
        final T singleResult = (T) q.getSingleResult();
        return singleResult;
    }
}
