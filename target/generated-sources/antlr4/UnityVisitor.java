// Generated from Unity.g4 by ANTLR 4.13.2

package com.metamadbooks.unity.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link UnityParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface UnityVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link UnityParser#unity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnity(UnityParser.UnityContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnityParser#unityElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnityElement(UnityParser.UnityElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnityParser#elementName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElementName(UnityParser.ElementNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnityParser#elementContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElementContent(UnityParser.ElementContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnityParser#attributesObject}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributesObject(UnityParser.AttributesObjectContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnityParser#attributePair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributePair(UnityParser.AttributePairContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnityParser#attributeValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeValue(UnityParser.AttributeValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnityParser#primitiveValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveValue(UnityParser.PrimitiveValueContext ctx);
}