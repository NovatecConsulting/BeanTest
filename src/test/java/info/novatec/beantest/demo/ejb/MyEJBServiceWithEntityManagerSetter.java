package info.novatec.beantest.demo.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MyEJBServiceWithEntityManagerSetter {
	
	private EntityManager em;
	
	@PersistenceContext(unitName = "db2")
	public void setEm(EntityManager em) {
		this.em = em;
	}
	
	public EntityManager getEm() {
		return em;
	}

}
