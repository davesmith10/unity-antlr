// Generated from Unity.g4 by ANTLR 4.13.2

package com.metamadbooks.unity.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link UnityParser}.
 */
public interface UnityListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link UnityParser#unity}.
	 * @param ctx the parse tree
	 */
	void enterUnity(UnityParser.UnityContext ctx);
	/**
	 * Exit a parse tree produced by {@link UnityParser#unity}.
	 * @param ctx the parse tree
	 */
	void exitUnity(UnityParser.UnityContext ctx);
	/**
	 * Enter a parse tree produced by {@link UnityParser#unityElement}.
	 * @param ctx the parse tree
	 */
	void enterUnityElement(UnityParser.UnityElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link UnityParser#unityElement}.
	 * @param ctx the parse tree
	 */
	void exitUnityElement(UnityParser.UnityElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link UnityParser#elementName}.
	 * @param ctx the parse tree
	 */
	void enterElementName(UnityParser.ElementNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link UnityParser#elementName}.
	 * @param ctx the parse tree
	 */
	void exitElementName(UnityParser.ElementNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link UnityParser#elementContent}.
	 * @param ctx the parse tree
	 */
	void enterElementContent(UnityParser.ElementContentContext ctx);
	/**
	 * Exit a parse tree produced by {@link UnityParser#elementContent}.
	 * @param ctx the parse tree
	 */
	void exitElementContent(UnityParser.ElementContentContext ctx);
	/**
	 * Enter a parse tree produced by {@link UnityParser#attributesObject}.
	 * @param ctx the parse tree
	 */
	void enterAttributesObject(UnityParser.AttributesObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link UnityParser#attributesObject}.
	 * @param ctx the parse tree
	 */
	void exitAttributesObject(UnityParser.AttributesObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link UnityParser#attributePair}.
	 * @param ctx the parse tree
	 */
	void enterAttributePair(UnityParser.AttributePairContext ctx);
	/**
	 * Exit a parse tree produced by {@link UnityParser#attributePair}.
	 * @param ctx the parse tree
	 */
	void exitAttributePair(UnityParser.AttributePairContext ctx);
	/**
	 * Enter a parse tree produced by {@link UnityParser#attributeValue}.
	 * @param ctx the parse tree
	 */
	void enterAttributeValue(UnityParser.AttributeValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link UnityParser#attributeValue}.
	 * @param ctx the parse tree
	 */
	void exitAttributeValue(UnityParser.AttributeValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link UnityParser#primitiveValue}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveValue(UnityParser.PrimitiveValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link UnityParser#primitiveValue}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveValue(UnityParser.PrimitiveValueContext ctx);
}