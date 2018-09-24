package org.adyachenko.customers_web;

import java.util.ArrayList; 

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import customer_core.service.CustomerDataService;
import customers_core.db.CommentDB;
import customers_core.db.CustomerDB;

public class CommentsView extends VerticalLayout {

	private static final String LABEL_SUBMIT_COMMENT = "Submit comment";

	private static final String LABEL_COMMENT_TEXT = "Comment text";

	private static final String LABEL_CUSTOMER_COMMENTS = "Customer Comments";

	private static final String STYLE_CUSTOMERS_COMMENT_LAYOUT = "customers-comment-layout";

	private static final String STYLE_CUSTOMERS_COMMENT_VIEW = "customers-comment-view";

	private static final String STYLE_CUSTOMER_COMMENT_TEXT = "customer-comment-text";

	CustomerDB customer;
	VerticalLayout commentsLayout;
	private static final long serialVersionUID = 5282568066585928348L;
	TextArea commentArea;


    private static final Logger logger = LogManager.getLogger(CommentsView.class);

	public CommentsView(CustomerDB customer) {
		this.addStyleName(STYLE_CUSTOMERS_COMMENT_VIEW);
		commentsLayout = new VerticalLayout();
		commentsLayout.addStyleName(STYLE_CUSTOMERS_COMMENT_LAYOUT);
		this.customer = customer;
		this.build();
	}

	private void build() {

		Label commentLabel = new Label(LABEL_CUSTOMER_COMMENTS);
		this.addComponent(commentLabel);
		commentArea = new TextArea(LABEL_COMMENT_TEXT);
		this.addComponent(commentArea);
		Button submitButton = new Button(LABEL_SUBMIT_COMMENT);

		CustomerDataService customerDataService = new CustomerDataService();

		submitButton.addClickListener(click -> {
			String commentText = commentArea.getValue();
			if (commentText != null && !commentText.isEmpty()) {
				CommentDB comment = new CommentDB();
				comment.setCustomer(customer);
				comment.setCommentText(commentArea.getValue());
				customerDataService.saveComment(comment);
				commentArea.clear();
				refreshComments(customerDataService);
			}
		});

		this.addComponent(submitButton);
		this.addComponent(commentsLayout);

		refreshComments(customerDataService);

	}

	private void refreshComments(CustomerDataService customerDataService) {

		this.commentsLayout.removeAllComponents();
		ArrayList<CommentDB> customerComments = customerDataService.getCommentsForCustomer(customer);

		if (customerComments != null && !customerComments.isEmpty()) {

			for (CommentDB comment : customerComments) {

				Panel commentWrapper = new Panel();
				Label commentText = new Label(comment.getCommentText(), ContentMode.PREFORMATTED);
				commentText.addStyleName(STYLE_CUSTOMER_COMMENT_TEXT);
				commentWrapper.setContent(commentText);
				commentsLayout.addComponent(commentWrapper);
			}
		}
	}

}
