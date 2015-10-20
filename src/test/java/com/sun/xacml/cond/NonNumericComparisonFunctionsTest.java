/**
 * Copyright (C) 2011-2015 Thales Services SAS.
 *
 * This file is part of AuthZForce.
 *
 * AuthZForce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthZForce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AuthZForce.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package com.sun.xacml.cond;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ExpressionType;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.sun.xacml.attr.DateAttribute;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.attr.TimeAttribute;
import com.sun.xacml.cond.xacmlv3.EvaluationResult;

@RunWith(Parameterized.class)
public class NonNumericComparisonFunctionsTest extends GeneralFunctionTest {

	private static final String NAME_STRING_GREATER_THAN = "urn:oasis:names:tc:xacml:1.0:function:string-greater-than";
	private static final String NAME_STRING_GREATER_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:string-greater-than-or-equal";
	private static final String NAME_STRING_LESS_THAN = "urn:oasis:names:tc:xacml:1.0:function:string-less-than";
	private static final String NAME_STRING_LESS_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:string-less-than-or-equal";
	private static final String NAME_TIME_GREATER_THAN = "urn:oasis:names:tc:xacml:1.0:function:time-greater-than";
	private static final String NAME_TIME_GREATER_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:time-greater-than-or-equal";
	private static final String NAME_TIME_LESS_THAN = "urn:oasis:names:tc:xacml:1.0:function:time-less-than";
	private static final String NAME_TIME_LESS_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:time-less-than-or-equal";
	private static final String NAME_TIME_IN_RANGE = "urn:oasis:names:tc:xacml:2.0:function:time-in-range";
	private static final String NAME_DATETIME_GREATER_THAN = "urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than";
	private static final String NAME_DATETIME_GREATER_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than-or-equal";
	private static final String NAME_DATETIME_LESS_THAN = "urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than";
	private static final String NAME_DATETIME_LESS_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than-or-equal";
	private static final String NAME_DATE_GREATER_THAN = "urn:oasis:names:tc:xacml:1.0:function:date-greater-than";
	private static final String NAME_DATE_GREATER_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:date-greater-than-or-equal";
	private static final String NAME_DATE_LESS_THAN = "urn:oasis:names:tc:xacml:1.0:function:date-less-than";
	private static final String NAME_DATE_LESS_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:date-less-than-or-equal";

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> params() throws Exception {
		return Arrays.asList(
				// urn:oasis:names:tc:xacml:1.0:function:string-greater-than
				new Object[] {
						NAME_STRING_GREATER_THAN,
						Arrays.asList(StringAttribute.getInstance("First"),
								StringAttribute.getInstance("Second")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_STRING_GREATER_THAN,
						Arrays.asList(StringAttribute.getInstance("Third"),
								StringAttribute.getInstance("Fourth")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_STRING_GREATER_THAN,
						Arrays.asList(StringAttribute.getInstance("Fifth"),
								StringAttribute.getInstance("Fifth")),
						EvaluationResult.getInstance(false) },

				// urn:oasis:names:tc:xacml:1.0:function:string-greater-than-or-equal
				new Object[] {
						NAME_STRING_GREATER_THAN_OR_EQUAL,
						Arrays.asList(StringAttribute.getInstance("First"),
								StringAttribute.getInstance("Second")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_STRING_GREATER_THAN_OR_EQUAL,
						Arrays.asList(StringAttribute.getInstance("Third"),
								StringAttribute.getInstance("Fourth")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_STRING_GREATER_THAN_OR_EQUAL,
						Arrays.asList(StringAttribute.getInstance("Fifth"),
								StringAttribute.getInstance("Fifth")),
						EvaluationResult.getInstance(true) },

				// urn:oasis:names:tc:xacml:1.0:function:string-less-than
				new Object[] {
						NAME_STRING_LESS_THAN,
						Arrays.asList(StringAttribute.getInstance("First"),
								StringAttribute.getInstance("Second")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_STRING_LESS_THAN,
						Arrays.asList(StringAttribute.getInstance("Third"),
								StringAttribute.getInstance("Fourth")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_STRING_LESS_THAN,
						Arrays.asList(StringAttribute.getInstance("Fifth"),
								StringAttribute.getInstance("Fifth")),
						EvaluationResult.getInstance(false) },

				// urn:oasis:names:tc:xacml:1.0:function:string-less-than-or-equal
				new Object[] {
						NAME_STRING_LESS_THAN_OR_EQUAL,
						Arrays.asList(StringAttribute.getInstance("First"),
								StringAttribute.getInstance("Second")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_STRING_LESS_THAN_OR_EQUAL,
						Arrays.asList(StringAttribute.getInstance("Third"),
								StringAttribute.getInstance("Fourth")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_STRING_LESS_THAN_OR_EQUAL,
						Arrays.asList(StringAttribute.getInstance("Fifth"),
								StringAttribute.getInstance("Fifth")),
						EvaluationResult.getInstance(true) },

				// urn:oasis:names:tc:xacml:1.0:function:time-greater-than
				new Object[] {
						NAME_TIME_GREATER_THAN,
						Arrays.asList(TimeAttribute.getInstance("09:30:15"),
								TimeAttribute.getInstance("09:44:22")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_TIME_GREATER_THAN,
						Arrays.asList(TimeAttribute.getInstance("09:30:15"),
								TimeAttribute.getInstance("08:50:48")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_TIME_GREATER_THAN,
						Arrays.asList(TimeAttribute.getInstance("09:30:15"),
								TimeAttribute.getInstance("09:30:15")),
						EvaluationResult.getInstance(false) },

				// urn:oasis:names:tc:xacml:1.0:function:time-greater-than-or-equal
				new Object[] {
						NAME_TIME_GREATER_THAN_OR_EQUAL,
						Arrays.asList(TimeAttribute.getInstance("09:30:15"),
								TimeAttribute.getInstance("09:44:22")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_TIME_GREATER_THAN_OR_EQUAL,
						Arrays.asList(TimeAttribute.getInstance("09:30:15"),
								TimeAttribute.getInstance("08:50:48")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_TIME_GREATER_THAN_OR_EQUAL,
						Arrays.asList(TimeAttribute.getInstance("09:30:15"),
								TimeAttribute.getInstance("09:30:15")),
						EvaluationResult.getInstance(true) },

				// urn:oasis:names:tc:xacml:1.0:function:time-less-than
				new Object[] {
						NAME_TIME_LESS_THAN,
						Arrays.asList(TimeAttribute.getInstance("09:30:15"),
								TimeAttribute.getInstance("09:44:22")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_TIME_LESS_THAN,
						Arrays.asList(TimeAttribute.getInstance("09:30:15"),
								TimeAttribute.getInstance("08:50:48")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_TIME_LESS_THAN,
						Arrays.asList(TimeAttribute.getInstance("09:30:15"),
								TimeAttribute.getInstance("09:30:15")),
						EvaluationResult.getInstance(false) },

				// urn:oasis:names:tc:xacml:1.0:function:time-less-than-or-equal
				new Object[] {
						NAME_TIME_LESS_THAN_OR_EQUAL,
						Arrays.asList(TimeAttribute.getInstance("09:30:15"),
								TimeAttribute.getInstance("09:44:22")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_TIME_LESS_THAN_OR_EQUAL,
						Arrays.asList(TimeAttribute.getInstance("09:30:15"),
								TimeAttribute.getInstance("08:50:48")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_TIME_LESS_THAN_OR_EQUAL,
						Arrays.asList(TimeAttribute.getInstance("09:30:15"),
								TimeAttribute.getInstance("09:30:15")),
						EvaluationResult.getInstance(true) },

				// urn:oasis:names:tc:xacml:2.0:function:time-in-range
				new Object[] {
						NAME_TIME_IN_RANGE,
						Arrays.asList(TimeAttribute.getInstance("09:30:15"),
								TimeAttribute.getInstance("09:30:00"),
								TimeAttribute.getInstance("09:45:00")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_TIME_IN_RANGE,
						Arrays.asList(TimeAttribute.getInstance("09:30:00"),
								TimeAttribute.getInstance("09:30:00"),
								TimeAttribute.getInstance("09:45:00")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_TIME_IN_RANGE,
						Arrays.asList(TimeAttribute.getInstance("09:45:00"),
								TimeAttribute.getInstance("09:30:00"),
								TimeAttribute.getInstance("09:45:00")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_TIME_IN_RANGE,
						Arrays.asList(TimeAttribute.getInstance("09:28:15"),
								TimeAttribute.getInstance("09:30:00"),
								TimeAttribute.getInstance("09:45:00")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_TIME_IN_RANGE,
						Arrays.asList(TimeAttribute.getInstance("09:47:15"),
								TimeAttribute.getInstance("09:30:00"),
								TimeAttribute.getInstance("09:45:00")),
						EvaluationResult.getInstance(false) },

				// urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than
				new Object[] {
						NAME_DATETIME_GREATER_THAN,
						Arrays.asList(DateTimeAttribute
								.getInstance("2002-09-24T09:30:15"),
								DateTimeAttribute
										.getInstance("2002-09-24T09:44:22")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_DATETIME_GREATER_THAN,
						Arrays.asList(DateTimeAttribute
								.getInstance("2002-09-24T09:30:15"),
								DateTimeAttribute
										.getInstance("2002-09-23T23:50:48")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_DATETIME_GREATER_THAN,
						Arrays.asList(DateTimeAttribute
								.getInstance("2002-09-24T09:30:15"),
								DateTimeAttribute
										.getInstance("2002-09-24T09:30:15")),
						EvaluationResult.getInstance(false) },

				// urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than-or-equal
				new Object[] {
						NAME_DATETIME_GREATER_THAN_OR_EQUAL,
						Arrays.asList(DateTimeAttribute
								.getInstance("2002-09-24T09:30:15"),
								DateTimeAttribute
										.getInstance("2002-09-24T09:44:22")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_DATETIME_GREATER_THAN_OR_EQUAL,
						Arrays.asList(DateTimeAttribute
								.getInstance("2002-09-24T09:30:15"),
								DateTimeAttribute
										.getInstance("2002-09-23T23:50:48")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_DATETIME_GREATER_THAN_OR_EQUAL,
						Arrays.asList(DateTimeAttribute
								.getInstance("2002-09-24T09:30:15"),
								DateTimeAttribute
										.getInstance("2002-09-24T09:30:15")),
						EvaluationResult.getInstance(true) },

				// urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than
				new Object[] {
						NAME_DATETIME_LESS_THAN,
						Arrays.asList(DateTimeAttribute
								.getInstance("2002-09-24T09:30:15"),
								DateTimeAttribute
										.getInstance("2002-09-24T09:44:22")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_DATETIME_LESS_THAN,
						Arrays.asList(DateTimeAttribute
								.getInstance("2002-09-24T09:30:15"),
								DateTimeAttribute
										.getInstance("2002-09-23T23:50:48")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_DATETIME_LESS_THAN,
						Arrays.asList(DateTimeAttribute
								.getInstance("2002-09-24T09:30:15"),
								DateTimeAttribute
										.getInstance("2002-09-24T09:30:15")),
						EvaluationResult.getInstance(false) },

				// urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than-or-equal
				new Object[] {
						NAME_DATETIME_LESS_THAN_OR_EQUAL,
						Arrays.asList(DateTimeAttribute
								.getInstance("2002-09-24T09:30:15"),
								DateTimeAttribute
										.getInstance("2002-09-24T09:44:22")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_DATETIME_LESS_THAN_OR_EQUAL,
						Arrays.asList(DateTimeAttribute
								.getInstance("2002-09-24T09:30:15"),
								DateTimeAttribute
										.getInstance("2002-09-23T23:50:48")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_DATETIME_LESS_THAN_OR_EQUAL,
						Arrays.asList(DateTimeAttribute
								.getInstance("2002-09-24T09:30:15"),
								DateTimeAttribute
										.getInstance("2002-09-24T09:30:15")),
						EvaluationResult.getInstance(true) },

				// urn:oasis:names:tc:xacml:1.0:function:date-greater-than
				new Object[] {
						NAME_DATE_GREATER_THAN,
						Arrays.asList(DateAttribute.getInstance("2002-09-24"),
								DateAttribute.getInstance("2002-09-25")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_DATE_GREATER_THAN,
						Arrays.asList(DateAttribute.getInstance("2002-09-24"),
								DateAttribute.getInstance("2002-09-23")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_DATE_GREATER_THAN,
						Arrays.asList(DateAttribute.getInstance("2002-09-24"),
								DateAttribute.getInstance("2002-09-24")),
						EvaluationResult.getInstance(false) },

				// urn:oasis:names:tc:xacml:1.0:function:date-greater-than-or-equal
				new Object[] {
						NAME_DATE_GREATER_THAN_OR_EQUAL,
						Arrays.asList(DateAttribute.getInstance("2002-09-24"),
								DateAttribute.getInstance("2002-09-25")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_DATE_GREATER_THAN_OR_EQUAL,
						Arrays.asList(DateAttribute.getInstance("2002-09-24"),
								DateAttribute.getInstance("2002-09-23")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_DATE_GREATER_THAN_OR_EQUAL,
						Arrays.asList(DateAttribute.getInstance("2002-09-24"),
								DateAttribute.getInstance("2002-09-24")),
						EvaluationResult.getInstance(true) },

				// urn:oasis:names:tc:xacml:1.0:function:date-less-than
				new Object[] {
						NAME_DATE_LESS_THAN,
						Arrays.asList(DateAttribute.getInstance("2002-09-24"),
								DateAttribute.getInstance("2002-09-25")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_DATE_LESS_THAN,
						Arrays.asList(DateAttribute.getInstance("2002-09-24"),
								DateAttribute.getInstance("2002-09-23")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_DATE_LESS_THAN,
						Arrays.asList(DateAttribute.getInstance("2002-09-24"),
								DateAttribute.getInstance("2002-09-24")),
						EvaluationResult.getInstance(false) },

				// urn:oasis:names:tc:xacml:1.0:function:date-less-than-or-equal
				new Object[] {
						NAME_DATE_LESS_THAN_OR_EQUAL,
						Arrays.asList(DateAttribute.getInstance("2002-09-24"),
								DateAttribute.getInstance("2002-09-25")),
						EvaluationResult.getInstance(true) },
				new Object[] {
						NAME_DATE_LESS_THAN_OR_EQUAL,
						Arrays.asList(DateAttribute.getInstance("2002-09-24"),
								DateAttribute.getInstance("2002-09-23")),
						EvaluationResult.getInstance(false) },
				new Object[] {
						NAME_DATE_LESS_THAN_OR_EQUAL,
						Arrays.asList(DateAttribute.getInstance("2002-09-24"),
								DateAttribute.getInstance("2002-09-24")),
						EvaluationResult.getInstance(true) });
	}

	public NonNumericComparisonFunctionsTest(String functionName,
			List<ExpressionType> inputs, EvaluationResult expectedResult)
			throws Exception {
		super(functionName, inputs, expectedResult);
	}

}