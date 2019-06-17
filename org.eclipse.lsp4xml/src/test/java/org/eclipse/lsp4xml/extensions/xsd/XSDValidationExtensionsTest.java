/**
 *  Copyright (c) 2018 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v20.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.lsp4xml.extensions.xsd;

import static org.eclipse.lsp4xml.XMLAssert.d;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4xml.XMLAssert;
import org.eclipse.lsp4xml.commons.BadLocationException;
import org.eclipse.lsp4xml.extensions.xsd.participants.XSDErrorCode;
import org.eclipse.lsp4xml.extensions.xsd.participants.diagnostics.XSDValidator;
import org.junit.Test;

/**
 * XSD diagnostics tests which test the {@link XSDValidator}.
 *
 */
public class XSDValidationExtensionsTest {

	@Test
	public void cos_all_limited_2() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<xs:schema \r\n" + //
				"	xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\r\n" + //
				"	\r\n" + //
				"	<xs:complexType name=\"testType\">\r\n" + //
				"		<xs:all>\r\n" + //
				"			<xs:element name=\"testEle1\" minOccurs=\"2\" maxOccurs=\"unbounded\" type=\"xs:string\"/>\r\n" + //
				"		</xs:all>\r\n" + //
				"	</xs:complexType>\r\n" + //
				"</xs:schema>";
		testDiagnosticsFor(xml, d(6, 55, 6, 66, XSDErrorCode.cos_all_limited_2));
	}

	@Test
	public void cos_all_limited_2_multiple() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<xs:schema \r\n" + //
				"	xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\r\n" + //
				"	\r\n" + //
				"	<xs:complexType name=\"testType\">\r\n" + //
				"		<xs:all>\r\n" + //
				"			<xs:element name=\"testEle1\" minOccurs=\"2\" maxOccurs=\"unbounded\" type=\"xs:string\"/>\r\n" + //
				"			<xs:element name=\"testEle2\" minOccurs=\"2\" maxOccurs=\"unbounded\" type=\"xs:string\"/>\r\n" + //
				"			<xs:element name=\"test3\" minOccurs=\"2\" maxOccurs=\"unbounded\" type=\"xs:string\"/>\r\n" + //
				"		</xs:all>\r\n" + //
				"	</xs:complexType>\r\n" + //
				"</xs:schema>";

		Diagnostic first = d(6, 55, 6, 66, XSDErrorCode.cos_all_limited_2);
		Diagnostic second = d(7, 55, 7, 66, XSDErrorCode.cos_all_limited_2);
		Diagnostic third = d(8, 52, 8, 63, XSDErrorCode.cos_all_limited_2);
		testDiagnosticsFor(xml, first, second, third);
	}

	@Test
	public void p_props_correct_2_1() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" +
				"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\r\n" +
				"	<xs:complexType name=\"testType\">\r\n" +
				"		<xs:all>\r\n" +
				"			<xs:element name=\"testEle\" minOccurs=\"1\" maxOccurs=\"0\" type=\"xs:string\"/>\r\n" +
				"		</xs:all>\r\n" +
				"	</xs:complexType>\r\n" +
				"</xs:schema>";
		testDiagnosticsFor(xml, d(4, 30, 4, 43, XSDErrorCode.p_props_correct_2_1));
	}

	@Test
	public void p_props_correct_2_1_multiple() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" +
			"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\r\n" +
			"	<xs:complexType name=\"testType\">\r\n" +
			"		<xs:all>\r\n" +
			"			<xs:element name=\"testEle\" minOccurs=\"1\" maxOccurs=\"0\" type=\"xs:string\"/>\r\n" +
			"			<xs:element name=\"test\" minOccurs=\"5\" maxOccurs=\"0\" type=\"xs:string\"/>\r\n" +
			"		</xs:all>\r\n" +
			"	</xs:complexType>\r\n" +
			"</xs:schema>";
		
		Diagnostic first = d(4, 30, 4, 43, XSDErrorCode.p_props_correct_2_1);
		Diagnostic second = d(5, 27, 5, 40, XSDErrorCode.p_props_correct_2_1);
		testDiagnosticsFor(xml, first, second);
	}


	@Test
	public void s4s_elt_invalid_content_1() throws BadLocationException {
		String xml = "<?xml version=\"1.1\"?>\r\n" + //
				"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\r\n" + //
				"	elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\r\n" + //
				"	<foo></foo>\r\n" + // <- error foo doesn't exist
				"</xs:schema>";
		testDiagnosticsFor(xml, d(3, 2, 3, 5, XSDErrorCode.s4s_elt_invalid_content_1));
	}

	@Test
	public void s4s_elt_character() throws BadLocationException {
		String xml = "<?xml version=\"1.1\"?>\r\n" + //
				"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\r\n" + //
				"	elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\r\n" + //
				"	<xs:element name=\"foo\">bar</xs:element>\r\n" + // <- error with bar text
				"</xs:schema>";
		testDiagnosticsFor(xml, d(3, 24, 3, 27, XSDErrorCode.s4s_elt_character));
	}

	@Test
	public void s4s_att_must_appear() throws BadLocationException {
		String xml = "<?xml version=\"1.1\"?>\r\n" + //
				"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\r\n" + //
				"	elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\r\n" + //
				"	<xs:element></xs:element>\r\n" + // <- error with @name missing
				"</xs:schema>";
		testDiagnosticsFor(xml, d(3, 2, 3, 12, XSDErrorCode.s4s_att_must_appear));
	}

	@Test
	public void s4s_att_not_allowed() throws BadLocationException {
		String xml = "<?xml version=\"1.1\"?>\r\n" + //
				"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\r\n" + //
				"	elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\r\n" + //
				"	<xs:element foo=\"bar\" ></xs:element>\r\n" + // <- error with foo attribute which is not allowed
				"</xs:schema>";
		testDiagnosticsFor(xml, d(3, 13, 3, 16, XSDErrorCode.s4s_att_not_allowed),
				d(3, 2, 3, 12, XSDErrorCode.s4s_att_must_appear));
	}

	@Test
	public void s4s_att_invalid_value() throws BadLocationException {
		String xml = "<?xml version=\"1.1\"?>\r\n" + //
				"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\r\n" + //
				"	elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\r\n" + //
				"	<xs:element name=\"\" ></xs:element>\r\n" + // <- error with @name which is empty
				"</xs:schema>";
		testDiagnosticsFor(xml, d(3, 18, 3, 20, XSDErrorCode.s4s_att_invalid_value),
				d(3, 2, 3, 12, XSDErrorCode.s4s_att_must_appear));
	}

	@Test
	public void src_resolve() throws BadLocationException {
		String xml = "<?xml version=\"1.1\"?>\r\n" + //
				"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\r\n" + //
				"	elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\r\n" + //
				"	<xs:element name=\"A\">\r\n" + //
				"		<xs:complexType>\r\n" + //
				"			<xs:sequence>\r\n" + //
				"				<xs:element name=\"A.1\" type=\"xs:string\" />\r\n" + //
				"				<xs:element name=\"A.2\" type=\"XXXXX\" /> \r\n" + // <- error with XXXXX
				"			</xs:sequence>\r\n" + //
				"		</xs:complexType>\r\n" + //
				"	</xs:element> \r\n" + //
				"</xs:schema>";
		testDiagnosticsFor(xml, d(7, 32, 7, 39, XSDErrorCode.src_resolve));
	}

	@Test
	public void src_resolve2() throws BadLocationException {
		String xml = "<?xml version=\"1.1\" ?>\r\n" + //
				"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\r\n" + //
				"	elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\r\n" + //
				"\r\n" + //
				"	<xs:simpleType name=\"carType\">\r\n" + //
				"		<xs:restriction base=\"xs:string\">\r\n" + //
				"			<xs:enumeration value=\"Audi\" />\r\n" + //
				"			<xs:enumeration value=\"Golf\" />\r\n" + //
				"			<xs:enumeration value=\"BMW\" />\r\n" + //
				"		</xs:restriction>\r\n" + //
				"	</xs:simpleType>\r\n" + //
				"\r\n" + //
				"	<xs:element name=\"car\" type=\"carType\" />\r\n" + //
				"	<xs:element name=\"foo\" type=\"fooType\" />\r\n" + // <- error with fooType which doesn't exists
				"\r\n" + //
				"</xs:schema>";
		testDiagnosticsFor(xml, d(13, 29, 13, 38, XSDErrorCode.src_resolve));
	}

	@Test
	public void src_element_2_1() throws BadLocationException {
		String xml = "<?xml version='1.0'?>\r\n" + //
				"<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>\r\n" + //
				"    <xs:element name='note'>\r\n" + //
				"        <xs:complexType>\r\n" + //
				"            <xs:sequence>\r\n" + //
				"                <xs:element nhame='to' type='xs:string' nillable='false' />\r\n" + // <- error nhame
																									// doesn't exists
				"                <xs:element name='from' type='xs:string' />\r\n" + //
				"                <xs:element name='heading' type='xs:string' />\r\n" + //
				"                <xs:element name='body' type='xs:string' nillable='false' />\r\n" + //
				"            </xs:sequence>\r\n" + //
				"        </xs:complexType>\r\n" + //
				"    </xs:element>\r\n" + //
				"</xs:schema>";
		testDiagnosticsFor(xml, d(5, 28, 5, 33, XSDErrorCode.s4s_att_not_allowed),
				d(5, 17, 5, 27, XSDErrorCode.src_element_2_1));
	}

	@Test
	public void src_element_3() throws BadLocationException {
		String xml = "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\r\n" +
			"  <xs:element name=\"a\" type=\"xs:integer\">\r\n" +
			"    <xs:complexType>\r\n" +
			"      <xs:sequence>\r\n" +
			"        <xs:element name=\"b\"></xs:element>\r\n" +
			"      </xs:sequence>\r\n" +
			"    </xs:complexType>\r\n" +
			"  </xs:element>\r\n" +
			"</xs:schema>";
		testDiagnosticsFor(xml, d(1, 3, 1, 13, XSDErrorCode.src_element_3));
	}

	private static void testDiagnosticsFor(String xml, Diagnostic... expected) throws BadLocationException {
		XMLAssert.testDiagnosticsFor(xml, null, null, "test.xsd", expected);
	}
}
