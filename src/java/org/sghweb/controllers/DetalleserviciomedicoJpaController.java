/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sghweb.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.sghweb.jpa.Medico;
import org.sghweb.jpa.Servicio;
import org.sghweb.jpa.Turno;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import org.sghweb.controllers.exceptions.IllegalOrphanException;
import org.sghweb.controllers.exceptions.NonexistentEntityException;
import org.sghweb.controllers.exceptions.PreexistingEntityException;
import org.sghweb.controllers.exceptions.RollbackFailureException;
import org.sghweb.jpa.Detalleserviciomedico;
import org.sghweb.jpa.DetalleserviciomedicoPK;

/**
 *
 * @author Roberto
 */
public class DetalleserviciomedicoJpaController implements Serializable {

    public DetalleserviciomedicoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "sgh-webPU") 
    private EntityManagerFactory emf = null;


    public EntityManager getEntityManager() {
        if (emf == null) { 
            emf = Persistence.createEntityManagerFactory("sgh-webPU"); 
        }
        return emf.createEntityManager();
    }

    public void create(Detalleserviciomedico detalleserviciomedico) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (detalleserviciomedico.getDetalleserviciomedicoPK() == null) {
            detalleserviciomedico.setDetalleserviciomedicoPK(new DetalleserviciomedicoPK());
        }
        if (detalleserviciomedico.getTurnoList() == null) {
            detalleserviciomedico.setTurnoList(new ArrayList<Turno>());
        }
        detalleserviciomedico.getDetalleserviciomedicoPK().setMedicodni(detalleserviciomedico.getMedico().getMedicoPK().getDni());
        detalleserviciomedico.getDetalleserviciomedicoPK().setServiciocodigo(detalleserviciomedico.getServicio().getCodigo());
        detalleserviciomedico.getDetalleserviciomedicoPK().setMedicocmp(detalleserviciomedico.getMedico().getMedicoPK().getCmp());
        EntityManager em = null;
        Context initCtx = new InitialContext(); 
        utx = (UserTransaction) initCtx.lookup("java:comp/UserTransaction");
        try {
            utx.begin();
            em = getEntityManager();
            Medico medico = detalleserviciomedico.getMedico();
            if (medico != null) {
                medico = em.getReference(medico.getClass(), medico.getMedicoPK());
                detalleserviciomedico.setMedico(medico);
            }
            Servicio servicio = detalleserviciomedico.getServicio();
            if (servicio != null) {
                servicio = em.getReference(servicio.getClass(), servicio.getCodigo());
                detalleserviciomedico.setServicio(servicio);
            }
            List<Turno> attachedTurnoList = new ArrayList<Turno>();
            for (Turno turnoListTurnoToAttach : detalleserviciomedico.getTurnoList()) {
                turnoListTurnoToAttach = em.getReference(turnoListTurnoToAttach.getClass(), turnoListTurnoToAttach.getTurnoPK());
                attachedTurnoList.add(turnoListTurnoToAttach);
            }
            detalleserviciomedico.setTurnoList(attachedTurnoList);
            em.persist(detalleserviciomedico);
            if (medico != null) {
                medico.getDetalleserviciomedicoList().add(detalleserviciomedico);
                medico = em.merge(medico);
            }
            if (servicio != null) {
                servicio.getDetalleserviciomedicoList().add(detalleserviciomedico);
                servicio = em.merge(servicio);
            }
            for (Turno turnoListTurno : detalleserviciomedico.getTurnoList()) {
                Detalleserviciomedico oldDetalleserviciomedicoOfTurnoListTurno = turnoListTurno.getDetalleserviciomedico();
                turnoListTurno.setDetalleserviciomedico(detalleserviciomedico);
                turnoListTurno = em.merge(turnoListTurno);
                if (oldDetalleserviciomedicoOfTurnoListTurno != null) {
                    oldDetalleserviciomedicoOfTurnoListTurno.getTurnoList().remove(turnoListTurno);
                    oldDetalleserviciomedicoOfTurnoListTurno = em.merge(oldDetalleserviciomedicoOfTurnoListTurno);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDetalleserviciomedico(detalleserviciomedico.getDetalleserviciomedicoPK()) != null) {
                throw new PreexistingEntityException("Detalleserviciomedico " + detalleserviciomedico + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Detalleserviciomedico detalleserviciomedico) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        detalleserviciomedico.getDetalleserviciomedicoPK().setMedicodni(detalleserviciomedico.getMedico().getMedicoPK().getDni());
        detalleserviciomedico.getDetalleserviciomedicoPK().setServiciocodigo(detalleserviciomedico.getServicio().getCodigo());
        detalleserviciomedico.getDetalleserviciomedicoPK().setMedicocmp(detalleserviciomedico.getMedico().getMedicoPK().getCmp());
        EntityManager em = null;
        Context initCtx = new InitialContext(); 
        utx = (UserTransaction) initCtx.lookup("java:comp/UserTransaction");
        try {
            utx.begin();
            em = getEntityManager();
            Detalleserviciomedico persistentDetalleserviciomedico = em.find(Detalleserviciomedico.class, detalleserviciomedico.getDetalleserviciomedicoPK());
            Medico medicoOld = persistentDetalleserviciomedico.getMedico();
            Medico medicoNew = detalleserviciomedico.getMedico();
            Servicio servicioOld = persistentDetalleserviciomedico.getServicio();
            Servicio servicioNew = detalleserviciomedico.getServicio();
            List<Turno> turnoListOld = persistentDetalleserviciomedico.getTurnoList();
            List<Turno> turnoListNew = detalleserviciomedico.getTurnoList();
            List<String> illegalOrphanMessages = null;
            for (Turno turnoListOldTurno : turnoListOld) {
                if (!turnoListNew.contains(turnoListOldTurno)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Turno " + turnoListOldTurno + " since its detalleserviciomedico field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (medicoNew != null) {
                medicoNew = em.getReference(medicoNew.getClass(), medicoNew.getMedicoPK());
                detalleserviciomedico.setMedico(medicoNew);
            }
            if (servicioNew != null) {
                servicioNew = em.getReference(servicioNew.getClass(), servicioNew.getCodigo());
                detalleserviciomedico.setServicio(servicioNew);
            }
            List<Turno> attachedTurnoListNew = new ArrayList<Turno>();
            for (Turno turnoListNewTurnoToAttach : turnoListNew) {
                turnoListNewTurnoToAttach = em.getReference(turnoListNewTurnoToAttach.getClass(), turnoListNewTurnoToAttach.getTurnoPK());
                attachedTurnoListNew.add(turnoListNewTurnoToAttach);
            }
            turnoListNew = attachedTurnoListNew;
            detalleserviciomedico.setTurnoList(turnoListNew);
            detalleserviciomedico = em.merge(detalleserviciomedico);
            if (medicoOld != null && !medicoOld.equals(medicoNew)) {
                medicoOld.getDetalleserviciomedicoList().remove(detalleserviciomedico);
                medicoOld = em.merge(medicoOld);
            }
            if (medicoNew != null && !medicoNew.equals(medicoOld)) {
                medicoNew.getDetalleserviciomedicoList().add(detalleserviciomedico);
                medicoNew = em.merge(medicoNew);
            }
            if (servicioOld != null && !servicioOld.equals(servicioNew)) {
                servicioOld.getDetalleserviciomedicoList().remove(detalleserviciomedico);
                servicioOld = em.merge(servicioOld);
            }
            if (servicioNew != null && !servicioNew.equals(servicioOld)) {
                servicioNew.getDetalleserviciomedicoList().add(detalleserviciomedico);
                servicioNew = em.merge(servicioNew);
            }
            for (Turno turnoListNewTurno : turnoListNew) {
                if (!turnoListOld.contains(turnoListNewTurno)) {
                    Detalleserviciomedico oldDetalleserviciomedicoOfTurnoListNewTurno = turnoListNewTurno.getDetalleserviciomedico();
                    turnoListNewTurno.setDetalleserviciomedico(detalleserviciomedico);
                    turnoListNewTurno = em.merge(turnoListNewTurno);
                    if (oldDetalleserviciomedicoOfTurnoListNewTurno != null && !oldDetalleserviciomedicoOfTurnoListNewTurno.equals(detalleserviciomedico)) {
                        oldDetalleserviciomedicoOfTurnoListNewTurno.getTurnoList().remove(turnoListNewTurno);
                        oldDetalleserviciomedicoOfTurnoListNewTurno = em.merge(oldDetalleserviciomedicoOfTurnoListNewTurno);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                DetalleserviciomedicoPK id = detalleserviciomedico.getDetalleserviciomedicoPK();
                if (findDetalleserviciomedico(id) == null) {
                    throw new NonexistentEntityException("The detalleserviciomedico with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(DetalleserviciomedicoPK id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        Context initCtx = new InitialContext(); 
        utx = (UserTransaction) initCtx.lookup("java:comp/UserTransaction");
        try {
            utx.begin();
            em = getEntityManager();
            Detalleserviciomedico detalleserviciomedico;
            try {
                detalleserviciomedico = em.getReference(Detalleserviciomedico.class, id);
                detalleserviciomedico.getDetalleserviciomedicoPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalleserviciomedico with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Turno> turnoListOrphanCheck = detalleserviciomedico.getTurnoList();
            for (Turno turnoListOrphanCheckTurno : turnoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Detalleserviciomedico (" + detalleserviciomedico + ") cannot be destroyed since the Turno " + turnoListOrphanCheckTurno + " in its turnoList field has a non-nullable detalleserviciomedico field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Medico medico = detalleserviciomedico.getMedico();
            if (medico != null) {
                medico.getDetalleserviciomedicoList().remove(detalleserviciomedico);
                medico = em.merge(medico);
            }
            Servicio servicio = detalleserviciomedico.getServicio();
            if (servicio != null) {
                servicio.getDetalleserviciomedicoList().remove(detalleserviciomedico);
                servicio = em.merge(servicio);
            }
            em.remove(detalleserviciomedico);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Detalleserviciomedico> findDetalleserviciomedicoEntities() {
        return findDetalleserviciomedicoEntities(true, -1, -1);
    }

    public List<Detalleserviciomedico> findDetalleserviciomedicoEntities(int maxResults, int firstResult) {
        return findDetalleserviciomedicoEntities(false, maxResults, firstResult);
    }

    private List<Detalleserviciomedico> findDetalleserviciomedicoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Detalleserviciomedico.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Detalleserviciomedico findDetalleserviciomedico(DetalleserviciomedicoPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Detalleserviciomedico.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleserviciomedicoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Detalleserviciomedico> rt = cq.from(Detalleserviciomedico.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
