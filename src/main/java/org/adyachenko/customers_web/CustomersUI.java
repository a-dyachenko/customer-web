package org.adyachenko.customers_web;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of an HTML page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class CustomersUI extends UI {

	private static final String STYLE_CUSTOMER_UI_LABEL = "customer-ui-label";

	private static final String LABEL_CUSTOMER_DATABASE_APPLICATION = "Customer Database Application";
	
	private static final long serialVersionUID = 6407949651043622490L;

	@Override
	protected void init(VaadinRequest vaadinRequest) {

		final VerticalLayout layout = new VerticalLayout();
		Label caption = new Label(LABEL_CUSTOMER_DATABASE_APPLICATION);
		caption.addStyleName(STYLE_CUSTOMER_UI_LABEL);
		layout.addComponent(caption);

		CustomerTable customerTable = new CustomerTable();

		layout.addComponent(customerTable);
		
		Button newCustomerButton = new Button(CustomerConstants.BUTTON_ADD_NEW_CUSTOMER);
		
		newCustomerButton.addClickListener(click -> {

			if (getWindows().size() == 0) {

				CustomerWindow.popupNewCustomerWindow(customerTable);  
			}

		});

		layout.addComponent(newCustomerButton);

		setContent(layout);
	}

	@WebServlet(urlPatterns = "/*", name = MyUIServlet.CUSTOMER_APP_SERVLET, asyncSupported = true)
	@VaadinServletConfiguration(ui = CustomersUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet { 
		static final String CUSTOMER_APP_SERVLET = "CustomerAppServlet";
		private static final long serialVersionUID = 8610851045526217362L;
	}

}
