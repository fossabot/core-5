/**
 * Copyright (C) 2012-2016 Thales Services SAS.
 *
 * This file is part of AuthZForce CE.
 *
 * AuthZForce CE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthZForce CE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AuthZForce CE.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package org.ow2.authzforce.core.pdp.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import net.sf.saxon.s9api.XPathCompiler;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignment;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpression;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ExpressionType;

import org.ow2.authzforce.core.pdp.api.EvaluationContext;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.expression.Expression;
import org.ow2.authzforce.core.pdp.api.expression.ExpressionFactory;
import org.ow2.authzforce.core.pdp.api.value.AttributeValue;
import org.ow2.authzforce.core.pdp.api.value.Bag;
import org.ow2.authzforce.core.pdp.api.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XACML AttributeAssignmentExpression evaluator
 *
 * @version $Id: $
 */
public class AttributeAssignmentExpressionEvaluator extends AttributeAssignmentExpression
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AttributeAssignmentExpressionEvaluator.class);

	private final transient Expression<?> evaluatableExpression;

	private static final UnsupportedOperationException UNSUPPORTED_SET_EXPRESSION_OPERATION_EXCEPTION = new UnsupportedOperationException("Unsupported operation: 'Expression' attribute is read-only");

	/*
	 * (non-Javadoc)
	 * 
	 * @see oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpression#getExpression()
	 */
	/** {@inheritDoc} */
	@Override
	public final JAXBElement<? extends ExpressionType> getExpression()
	{
		return evaluatableExpression.getJAXBElement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpression#setExpression(javax .xml.bind.JAXBElement)
	 */
	/** {@inheritDoc} */
	@Override
	public final void setExpression(JAXBElement<? extends ExpressionType> value)
	{
		throw UNSUPPORTED_SET_EXPRESSION_OPERATION_EXCEPTION;
	}

	/**
	 * Instantiates evaluatable AttributeAssignment expression from XACML-Schema-derived JAXB {@link oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpression}
	 *
	 * @param jaxbAttrAssignExp
	 *            XACML-schema-derived JAXB AttributeAssignmentExpression
	 * @param xPathCompiler
	 *            XPath compiler corresponding to enclosing policy(set) default XPath version
	 * @param expFactory
	 *            expression factory for parsing the AttributeAssignmentExpression's expression
	 * @throws java.lang.IllegalArgumentException
	 *             invalid AttributeAssignmentExpression's Expression
	 */
	public AttributeAssignmentExpressionEvaluator(AttributeAssignmentExpression jaxbAttrAssignExp, XPathCompiler xPathCompiler, ExpressionFactory expFactory) throws IllegalArgumentException
	{
		// JAXB fields
		this.attributeId = jaxbAttrAssignExp.getAttributeId();
		this.category = jaxbAttrAssignExp.getCategory();
		this.issuer = jaxbAttrAssignExp.getIssuer();
		/*
		 * Set JAXB field to null, getExpression() overridden and setExpression() not allowed instead
		 */
		this.expression = null;
		// END OF JAXB fields

		this.evaluatableExpression = expFactory.getInstance(jaxbAttrAssignExp.getExpression().getValue(), xPathCompiler, null);
	}

	/**
	 * Evaluates to AttributeAssignments Section 5.39 and 5.40 of XACML 3.0 core spec: If an AttributeAssignmentExpression evaluates to an atomic attribute value, then there MUST be one resulting
	 * AttributeAssignment which MUST contain this single attribute value. If the AttributeAssignmentExpression evaluates to a bag, then there MUST be a resulting AttributeAssignment for each of the
	 * values in the bag. If the bag is empty, there shall be no AttributeAssignment from this AttributeAssignmentExpression
	 *
	 * @param context
	 *            evaluation context
	 * @return non-null AttributeAssignments; empty if no AttributeValue resulting from evaluation of the Expression
	 * @throws org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException
	 *             if evaluation of the Expression in this context fails (Indeterminate)
	 */
	public List<AttributeAssignment> evaluate(EvaluationContext context) throws IndeterminateEvaluationException
	{
		final Value result = this.evaluatableExpression.evaluate(context);
		LOGGER.debug("AttributeAssignmentExpression[Category={},Issuer={},Id={}]/Expression -> {}", this.category, this.issuer, this.attributeId, result);

		final List<AttributeAssignment> attrAssignList = new ArrayList<>();
		if (result instanceof Bag)
		{
			// result is a bag
			final Bag<?> bag = (Bag<?>) result;
			/*
			 * Bag may be empty, in particular if AttributeDesignator/AttributeSelector with MustBePresent=False evaluates to empty bag. Sections 5.30/5.40 of XACML core spec says:
			 * "If the bag is empty, there shall be no <AttributeAssignment> from this <AttributeAssignmentExpression>."
			 */
			for (final AttributeValue attrVal : bag)
			{
				final AttributeAssignment attrAssignment = new AttributeAssignment(attrVal.getContent(), attrVal.getDataType(), attrVal.getOtherAttributes(), this.attributeId, this.category,
						this.issuer);
				attrAssignList.add(attrAssignment);
			}
		} else
		{
			// atomic (see spec §5.30, 5.40) / primitive attribute value
			final AttributeValue attrVal = (AttributeValue) result;
			final AttributeAssignment attrAssignment = new AttributeAssignment(attrVal.getContent(), attrVal.getDataType(), attrVal.getOtherAttributes(), this.attributeId, this.category, this.issuer);
			attrAssignList.add(attrAssignment);
		}

		return attrAssignList;
	}

	// public static void main(String[] args) throws JAXBException
	// {
	// THIS WILL FAIL: com.sun.istack.internal.SAXException2: unable to marshal type "java.lang.Double" as an element because it is missing an
	// @XmlRootElement annotation; but it succeeds with java.lang.String
	// final AttributeAssignment attrAssignment = new AttributeAssignment(Collections.<Serializable> singletonList("1.0"), "mytype", null, "myattribute1",
	// "mycategory", null);
	//
	// Marshaller marshaller = XACMLBindingUtils.createXacml3Marshaller();
	// marshaller.marshal(attrAssignment, System.out);
	// }

}
