/**
 *
 *  Copyright 2003-2004 Sun Microsystems, Inc. All Rights Reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *    1. Redistribution of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *
 *    2. Redistribution in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *  Neither the name of Sun Microsystems, Inc. or the names of contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  This software is provided "AS IS," without a warranty of any kind. ALL
 *  EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 *  ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 *  OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 *  AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 *  AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 *  DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 *  REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 *  INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 *  OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 *  EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 *  You acknowledge that this software is not designed or intended for use in
 *  the design, construction, operation or maintenance of any nuclear facility.
 */
package com.sun.xacml;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Advice;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpression;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AssociatedAdvice;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignment;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpression;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.CombinerParametersType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xacml.combine.CombinerElement;
import com.sun.xacml.combine.CombinerParameter;
import com.sun.xacml.combine.CombiningAlgFactory;
import com.sun.xacml.combine.CombiningAlgorithm;
import com.sun.xacml.combine.PolicyCombinerElement;
import com.sun.xacml.combine.PolicyCombiningAlgorithm;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.xacmlv3.AdviceExpressions;
import com.sun.xacml.xacmlv3.IPolicy;
import com.sun.xacml.xacmlv3.Target;
import com.thalesgroup.authzforce.BindingUtility;

/**
 * Represents an instance of an XACML policy.
 * 
 * @since 1.0
 * @author Seth Proctor
 * @author Marco Barreno
 */
public abstract class AbstractPolicySet extends oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySet implements IPolicy {
	
	// atributes associated with this policy
	// private URI idAttr;
	// private String version;
	private CombiningAlgorithm combiningAlg;

	// the elements in the policy
	// private String description;
	// private TargetType target;

	// the value in defaults, or null if there was no default value
	private String defaultVersion;

	// the meta-data associated with this policy
	private PolicyMetaData metaData;

	// the child elements under this policy represented simply as the
	// PolicyTreeElements...
	private List children;
	// ...or the CombinerElements that are passed to combining algorithms
	private List childElements;

	// any obligations held by this policy
	// private Set obligations;

	// any obligations held by this policy
	// private Set advice;

	// the list of combiner parameters
	// private List parameters;

	// the logger we'll use for all messages
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractPolicySet.class);

	/**
	 * Constructor used by <code>PolicyReference</code>, which supplies its own
	 * values for the methods in this class.
	 */
	protected AbstractPolicySet() {

	}

	/**
	 * Constructor used to create a policy from concrete components.
	 * 
	 * @param id
	 *            the policy id
	 * @param version
	 *            the policy version or null for the default (this is always
	 *            null for pre-2.0 policies)
	 * @param combiningAlg
	 *            the combining algorithm to use
	 * @param description
	 *            describes the policy or null if there is none
	 * @param target
	 *            the policy's target
	 */
	protected AbstractPolicySet(URI id, String version,
			PolicyCombiningAlgorithm combiningAlg, String description,
			oasis.names.tc.xacml._3_0.core.schema.wd_17.Target target) {
		this(id, version, combiningAlg, description, target, null);
	}

	/**
	 * Constructor used to create a policy from concrete components.
	 * 
	 * @param id
	 *            the policy id
	 * @param version
	 *            the policy version or null for the default (this is always
	 *            null for pre-2.0 policies)
	 * @param combiningAlg
	 *            the combining algorithm to use
	 * @param description
	 *            describes the policy or null if there is none
	 * @param target
	 *            the policy's target
	 * @param defaultVersion
	 *            the XPath version to use for selectors
	 */
	protected AbstractPolicySet(URI id, String version,
			PolicyCombiningAlgorithm combiningAlg, String description,
			oasis.names.tc.xacml._3_0.core.schema.wd_17.Target target, String defaultVersion) {
		this(id, version, combiningAlg, description, target, defaultVersion,
				null, null, null);
	}

	/**
	 * Constructor used to create a policy from concrete components.
	 * 
	 * @param id
	 *            the policy id
	 * @param version
	 *            the policy version or null for the default (this is always
	 *            null for pre-2.0 policies)
	 * @param combiningAlg
	 *            the combining algorithm to use
	 * @param description
	 *            describes the policy or null if there is none
	 * @param target
	 *            the policy's target
	 * @param defaultVersion
	 *            the XPath version to use for selectors
	 * @param obligations
	 *            the policy's obligations
	 */
	protected AbstractPolicySet(URI id, String version,
			PolicyCombiningAlgorithm combiningAlg, String description,
			oasis.names.tc.xacml._3_0.core.schema.wd_17.Target target, String defaultVersion,
			oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressions obligations,
			oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressions advices, List parameters) {
		this.policySetId = id.toASCIIString();
		this.combiningAlg = combiningAlg;
		this.description = description;
		this.target = target;
		this.defaultVersion = defaultVersion;

		if (version == null) {
			this.version = "1.0";
		} else {
			this.version = version;
		}

		metaData = new PolicyMetaData(PolicyMetaData.XACML_3_0_IDENTIFIER, defaultVersion);

		if (obligations == null) {
			/*
			 * obligationExpressions must be null by default, if you create new
			 * ObligationExpressions() in this case, the result Obligations will be marshalled to
			 * empty <Obligations /> element which is NOT VALID against the XACML schema.
			 */
			this.obligationExpressions = null;
		} else {
			this.obligationExpressions = obligations;
		}

		if (advices == null) {
			/*
			 * adviceExpressions must be null by default, if you create new
			 * AdviceExpressions() in this case, the result AssociatedAdvice will be marshalled to
			 * empty <AssociatedAdvice /> element which is NOT VALID against the XACML schema.
			 */
			this.adviceExpressions = null;
		} else {
			this.adviceExpressions = advices;
		}

		if (parameters == null) {
			this.policySetsAndPoliciesAndPolicySetIdReferences = Collections.EMPTY_LIST;
		} else {
			this.policySetsAndPoliciesAndPolicySetIdReferences = Collections
					.unmodifiableList(new ArrayList(parameters));
		}
	}

	/**
	 * Constructor used by child classes to initialize the shared data from a
	 * DOM root node.
	 * 
	 * @param root
	 *            the DOM root of the policy
	 * @param policyPrefix
	 *            either "Policy" or "PolicySet"
	 * @param combiningName
	 *            name of the field naming the combining alg
	 * 
	 * @throws ParsingException
	 *             if the policy is invalid
	 */
	protected AbstractPolicySet(Node root, String policyPrefix,
			String combiningName) throws ParsingException {
		// get the attributes, all of which are common to Policies
		NamedNodeMap attrs = root.getAttributes();

		try {
			// get the attribute Id
			this.policySetId = attrs.getNamedItem(policyPrefix + "Id")
					.getNodeValue();
		} catch (Exception e) {
			throw new ParsingException("Error parsing required attribute "
					+ policyPrefix + "Id", e);
		}

		// see if there's a version
		Node versionNode = attrs.getNamedItem("Version");
		if (versionNode != null) {
			version = versionNode.getNodeValue();
		} else {
			// assign the default version
			version = "1.0";
		}

		// now get the combining algorithm...
		try {
			URI algId = new URI(attrs.getNamedItem(combiningName)
					.getNodeValue());
			CombiningAlgFactory factory = CombiningAlgFactory.getInstance();
			combiningAlg = factory.createAlgorithm(algId);
			this.policyCombiningAlgId = attrs.getNamedItem(combiningName)
					.getNodeValue();
		} catch (Exception e) {
			throw new ParsingException(
					"Error parsing policy combining algorithm" + " in "
							+ policyPrefix, e);
		}

		// ...and make sure it's the right kind
		if (policyPrefix.equals("PolicySet")) {
			if (!(combiningAlg instanceof PolicyCombiningAlgorithm))
				throw new ParsingException("PolicySet (id=" + this.policySetId + ") must use a Policy "
						+ "Combining Algorithm");
		}

		// do an initial pass through the elements to pull out the
		// defaults, if any, so we can setup the meta-data
		NodeList _children = root.getChildNodes();

		for (int i = 0; i < _children.getLength(); i++) {
			Node child = _children.item(i);
			if (child.getNodeName().equals(policyPrefix + "Defaults"))
				handleDefaults(child);
		}

		// with the defaults read, create the meta-data
		metaData = new PolicyMetaData(root.getNamespaceURI(), defaultVersion);

		// now read the remaining policy elements
		/*
		 * obligationExpressions must be null by default, if you create new
		 * ObligationExpressions() in this case, the result Obligations will be marshalled to
		 * empty <Obligations /> element which is NOT VALID against the XACML schema.
		 */
		obligationExpressions = null;
		this.policySetsAndPoliciesAndPolicySetIdReferences = new ArrayList();
		/*
		 * obligationExpressions must be null by default, if you create new
		 * AdviceExpressions() in this case, the result AssociatedAdvice will be marshalled to
		 * empty <AssociatedAdvice /> element which is NOT VALID against the XACML schema.
		 */
		adviceExpressions = null;
		_children = root.getChildNodes();
		List myPolicies = new ArrayList();

		for (int i = 0; i < _children.getLength(); i++) {
			Node child = _children.item(i);
			String cname = child.getNodeName();

			if (cname.equals("Description")) {
				if (child.hasChildNodes())
					description = child.getFirstChild().getNodeValue();
			} else if (cname.equals("Target")) {
				target = Target.getInstance(child, metaData);
			} else if (cname.equals("Obligations")) {
				parseObligations(child);
			} else if (cname.equals("CombinerParameters")) {
				this.policySetsAndPoliciesAndPolicySetIdReferences = handleParameters(child);
			} else if (cname.equals("ObligationExpressions")) {
//				parseObligations(child);
				this.obligationExpressions = ObligationExpressions.getInstance(child);
			} else if (cname.equals("AdviceExpressions")) {
//				parseAdvicesExpressions(child);
				this.adviceExpressions = AdviceExpressions.getInstance(child);
			} else if (cname.equals("Policy")) {
				// myPolicies.add(Policy.getInstance(child));
			}
		}
		// this.policySetOrPolicyOrPolicySetIdReference.addAll((List<JAXBElement<?>>)
		// Collections.unmodifiableList(myPolicies));

		// finally, make sure the obligations and parameters are immutable
		// ObligationExpressionsType oblExpr = new ObligationExpressionsType();
		// oblExpr.getObligationExpression().addAll(obligations);
		// obligationExpressions = oblExpr;
		// this.combinerParametersOrRuleCombinerParametersOrVariableDefinition =
		// Collections.unmodifiableList(parameters);
		// adviceExpressions = Collections.unmodifiableSet(advice);

		// this.adviceExpressions = AdviceExpressions.getInstance(advice);
		// this.obligationExpressions =
		// ObligationExpressions.getInstance(obligations);
	}

	/**
	 * Helper routine to parse the obligation data
	 */
	private void parseObligations(Node root) throws ParsingException {
		NodeList nodes = root.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeName().equals("ObligationExpression")) {
				JAXBElement<oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressions> match = null;
				try {
					Unmarshaller u = BindingUtility.XACML3_0_JAXB_CONTEXT.createUnmarshaller();
					match = u.unmarshal(root, oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressions.class);
					obligationExpressions = match.getValue();
				} catch (Exception e) {
					LOGGER.error("Error unmarshalling ObligationExpressions", e);
				}
				
				break;
			}
		}
	}

	/**
	 * Helper routine to parse the obligation data
	 */
	private void parseAdvicesExpressions(Node root) throws ParsingException {
		NodeList nodes = root.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeName().equals("AdviceExpressions")) {
				JAXBElement<oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressions> match = null;
				try {
					Unmarshaller u = BindingUtility.XACML3_0_JAXB_CONTEXT.createUnmarshaller();
					match = u.unmarshal(root, oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressions.class);
					adviceExpressions = match.getValue();
				} catch (Exception e) {
					LOGGER.error("Error unmarshalling AdviceExpressions", e);
				}

				break;
			}
		}
	}

	/**
	 * There used to be multiple things in the defaults type, but now there's
	 * just the one string that must be a certain value, so it doesn't seem all
	 * that useful to have a class for this...we could always bring it back,
	 * however, if it started to do more
	 */
	private void handleDefaults(Node root) throws ParsingException {
		defaultVersion = null;
		NodeList nodes = root.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeName().equals("XPathVersion"))
				defaultVersion = node.getFirstChild().getNodeValue();
		}
	}

	/**
	 * Handles all the CombinerParameters in the policy or policy set
	 */
	private List handleParameters(Node root) throws ParsingException {
		NodeList nodes = root.getChildNodes();
		List parameters = new ArrayList();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeName().equals("CombinerParameter"))
				parameters.add(CombinerParameter.getInstance(node));
		}

		return parameters;
	}

	/**
	 * Returns the id of this policy
	 * 
	 * @return the policy id
	 */
	public URI getId() {
		if (policySetId != null) {
			return URI.create(policySetId);
		} else {
			return null;
		}
	}

	/**
	 * Returns the version of this policy. If this is an XACML 1.x policy then
	 * this will always return <code>"1.0"</code>.
	 * 
	 * @return the policy version
	 */
	public String getVersion() {
		return version;
	}

	@Override
	public CombiningAlgorithm getCombiningAlg() {
		return combiningAlg;
	}

	/**
	 * Returns the list of input parameters for the combining algorithm. If this
	 * is an XACML 1.x policy then the list will always be empty.
	 * 
	 * @return a <code>List</code> of <code>CombinerParameter</code>s
	 */
	public List getCombiningParameters() {
		return this.policySetsAndPoliciesAndPolicySetIdReferences;
	}

	/**
	 * Returns the given description of this policy or null if there is no
	 * description
	 * 
	 * @return the description or null
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the target for this policy
	 * 
	 * @return the policy's target
	 */
	public oasis.names.tc.xacml._3_0.core.schema.wd_17.Target getTarget() {
		return target;
	}

	/**
	 * Returns the XPath version to use or null if none was specified
	 * 
	 * @return XPath version or null
	 */
	public String getDefaultVersion() {
		return defaultVersion;
	}

	/**
	 * Returns the <code>List</code> of children under this node in the policy
	 * tree. Depending on what kind of policy this node represents the children
	 * will either be <code>AbstractPolicy</code> objects or <code>Rule</code>s.
	 * 
	 * @return a <code>List</code> of child nodes
	 */
	@Override
	public List getChildren() {
		return policySetsAndPoliciesAndPolicySetIdReferences;
	}

	/**
	 * Returns the <code>List</code> of <code>CombinerElement</code>s that is
	 * provided to the combining algorithm. This returns the same set of
	 * children that <code>getChildren</code> provides along with any associated
	 * combiner parameters.
	 * 
	 * @return a <code>List</code> of <code>CombinerElement</code>s
	 */
	@Override
	public List getChildElements() {
		return policySetsAndPoliciesAndPolicySetIdReferences;
	}

	/**
	 * Returns the Set of obligations for this policy, which may be empty
	 * 
	 * @return the policy's obligations
	 */
	public oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressions getObligations() {
		return obligationExpressions;
	}

	/**
	 * Returns the meta-data associated with this policy
	 */
	@Override
	public PolicyMetaData getMetaData() {
		return metaData;
	}

	/**
	 * Given the input context sees whether or not the request matches this
	 * policy. This must be called by combining algorithms before they evaluate
	 * a policy. This is also used in the initial policy finding operation to
	 * determine which top-level policies might apply to the request.
	 * 
	 * @param context
	 *            the representation of the request
	 * 
	 * @return the result of trying to match the policy and the request
	 */
	@Override
	public MatchResult match(EvaluationCtx context) {
		/**
		 * Romain Ferrari (Thales)
		 * 
		 * @BUG: NPE
		 */
		if (target == null) {
			throw new RuntimeException("No target found in policySet with id="
					+ policySetId);
		}

		return ((Target) target).match(context);
	}

	/**
	 * FIXME: remove this method since PolicySet cannot have Rules as children, and this seems not to be used by anyone.
	 * 
	 * Sets the child policy tree elements for this node, which are passed to
	 * the combining algorithm on evaluation. The <code>List</code> must contain
	 * <code>CombinerElement</code>s, which in turn will contain
	 * <code>Rule</code>s or <code>AbstractPolicy</code>s, but may not contain
	 * both types of elements.
	 * 
	 * @param children
	 *            a <code>List</code> of <code>CombinerElement</code>s
	 *            representing the child elements used by the combining
	 *            algorithm
	 */
	protected void setChildren(List<Rule> children) {
		// we always want a concrete list, since we're going to pass it to
		// a combiner that expects a non-null input
		if (children == null) {
			this.children = Collections.EMPTY_LIST;
		} else {
			// NOTE: since this is only getting called by known child
			// classes we don't check that the types are all the same
			// List list = new ArrayList();
			// Iterator it = children.iterator();
			//
			// while (it.hasNext()) {
			// CombinerElement element = (CombinerElement) (it.next());
			// list.add(element.getElement());
			// }

			this.children = Collections.unmodifiableList(children);
			childElements = Collections.unmodifiableList(children);
		}
	}

	/**
	 * Tries to evaluate the policyset by calling the combining algorithm on the
	 * given policies. The <code>match</code> method must always be called
	 * first, and must always return MATCH, before this method is called.
	 * 
	 * @param context
	 *            the representation of the request
	 * 
	 * @return the result of evaluation
	 */
	@Override
	public Result evaluate(EvaluationCtx context) {
		Result result = null;
		List<IPolicy> policies = new ArrayList<>();
		CombinerParametersType combParams = new CombinerParametersType();
		for (Object element : this.policySetsAndPoliciesAndPolicySetIdReferences) {
			if (element instanceof PolicyCombinerElement) {
				final Object combinerElt = ((PolicyCombinerElement) element).getElement();
				if (combinerElt instanceof IPolicy) {	
					policies.add((IPolicy) combinerElt);
				} else {
					continue;
				}

			}
		}
		// evaluate
		result = this.combiningAlg.combine(context, combParams, policies);

		if (obligationExpressions != null && !obligationExpressions.getObligationExpressions().isEmpty()) {
			// now, see if we should add any obligations to the set
			int effect = result.getDecision().ordinal();

			if ((effect == DecisionType.INDETERMINATE.ordinal())
					|| (effect == DecisionType.NOT_APPLICABLE.ordinal())) {
				// we didn't permit/deny, so we never return obligations
				return result;
			}

			for (ObligationExpression myObligation : obligationExpressions
					.getObligationExpressions()) {
				if (myObligation.getFulfillOn().ordinal() == effect) {
					result.addObligation(myObligation, context);
				}
			}
		}
		/* If we have advice, it's definitely a 3.0 policy */
		if (adviceExpressions != null  && !adviceExpressions.getAdviceExpressions().isEmpty()) {
			int effect = result.getDecision().ordinal();

			if ((effect == DecisionType.INDETERMINATE.ordinal()) || (effect == DecisionType.NOT_APPLICABLE.ordinal())) {
				// we didn't permit/deny, so we never return advices
				return result;
			}
			
			final AssociatedAdvice returnAssociatedAdvice = result.getAssociatedAdvice();
			final AssociatedAdvice newAssociatedAdvice;
			if(returnAssociatedAdvice == null) {
				newAssociatedAdvice = new AssociatedAdvice();
				result.setAssociatedAdvice(newAssociatedAdvice);
			} else {
				newAssociatedAdvice = returnAssociatedAdvice;
			}
			
			for (AdviceExpression adviceExpr : adviceExpressions.getAdviceExpressions()) {
				if (adviceExpr.getAppliesTo().ordinal() == effect) {
					Advice advice = new Advice();
					advice.setAdviceId(adviceExpr.getAdviceId());
					for (AttributeAssignmentExpression attrExpr : adviceExpr
							.getAttributeAssignmentExpressions()) {
						AttributeAssignment myAttrAssType = new AttributeAssignment();
						myAttrAssType.setAttributeId(attrExpr.getAttributeId());
						myAttrAssType.setCategory(attrExpr.getCategory());
						myAttrAssType.getContent()
								.add(attrExpr.getExpression());
						myAttrAssType.setIssuer(attrExpr.getIssuer());
						advice.getAttributeAssignments().add(myAttrAssType);
					}
					
					newAssociatedAdvice.getAdvices().add(advice);
				}
			}
		}
		if (context.getIncludeInResults().size() > 0) {
			result.getAttributes().addAll(context.getIncludeInResults());
		}

		return result;
	}

	/**
	 * Routine used by <code>Policy</code> and <code>PolicySet</code> to encode
	 * some common elements.
	 * 
	 * @param output
	 *            a stream into which the XML-encoded data is written
	 * @param indenter
	 *            an object that creates indentation strings
	 */
	protected void encodeCommonElements(OutputStream output, Indenter indenter) {
		Iterator it = childElements.iterator();
		while (it.hasNext()) {
			((CombinerElement) (it.next())).encode(output, indenter);
		}

		if (obligationExpressions != null && !obligationExpressions.getObligationExpressions().isEmpty()) {
			PrintStream out = new PrintStream(output);
			String indent = indenter.makeString();

			out.println(indent + "<Obligations>");
			indenter.in();

			it = obligationExpressions.getObligationExpressions().iterator();
			while (it.hasNext()) {
				((Obligation) (it.next())).encode(output, indenter);
			}

			out.println(indent + "</Obligations>");
			indenter.out();
		}
		
		if (adviceExpressions != null && !adviceExpressions.getAdviceExpressions().isEmpty()) {
			PrintStream out = new PrintStream(output);
			String indent = indenter.makeString();

			out.println(indent + "<AssociatedAdvice>");
			indenter.in();

			it = adviceExpressions.getAdviceExpressions().iterator();
			while (it.hasNext()) {
				((Obligation) (it.next())).encode(output, indenter);
			}

			out.println(indent + "</AssociatedAdvice>");
			indenter.out();
		}
	}

	@Override
	public String toString() {
		String className = this.getClass().getSimpleName();
		return className + " id: \"" + policySetId + "\"";
	}

}
