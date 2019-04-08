package org.adyachenko.customers_web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.hibernate.Session;

import customers_core.dao.HibernateUtil;

/**
 * web filter for committing hibernate transaction / closing session
 * 
 * @author adyachenko
 *
 */
@WebFilter(filterName = "hibernateFilter")
public class HibernateFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		chain.doFilter(request, response);
		Session session = HibernateUtil.getSession();
		try {
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (session.getTransaction() != null) {
				System.out.println("rolling back transaction");
				session.getTransaction().rollback();
			}
		} finally {
			session.close();
		}

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
