/*
 * Copyright 2014 - 2017 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.mrapp.android.preference.activity.parser;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import de.mrapp.android.preference.activity.PreferenceHeader;
import de.mrapp.android.preference.activity.R;

/**
 * An utility class, which allows to parse instances of the class {@link PreferenceHeader} from XML
 * files.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public final class PreferenceHeaderParser {

    /**
     * The name of the start tag of a XML file, which defines multiple preference headers.
     */
    private static final String START_TAG_NAME = "preference-headers";

    /**
     * The name of a XML tag, which defines a preference header.
     */
    private static final String PREFERENCE_HEADER_TAG_NAME = "header";

    /**
     * The name of a XML tag, which defines an intent.
     */
    private static final String INTENT_TAG_NAME = "intent";

    /**
     * The name of a XML tag, which defines a bundle.
     */
    private static final String BUNDLE_TAG_NAME = "extra";

    /**
     * Creates a new utility class, which allows to parse instances of the class {@link
     * PreferenceHeader} from XML files.
     */
    private PreferenceHeaderParser() {

    }

    /**
     * Parses and returns the preference headers by using a specific XML resource parser.
     *
     * @param context
     *         The context, which should be used for parsing, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param parser
     *         The parser, which should be used, as an instance of the type {@link XmlPullParser}.
     *         The parser may not be null
     * @return A collection, which contains the preference headers, which have been parsed from the
     * given XML resource, as an instance of the type {@link Collection}
     * @throws XmlPullParserException
     *         The exception, which is thrown, if an error occurs while parsing the XML file	 *
     * @throws IOException
     *         The exception, which is thrown, if an error occurs while accessing the XML file
     */
    private static Collection<PreferenceHeader> parsePreferenceHeaders(
            @NonNull final Context context, @NonNull final XmlResourceParser parser)
            throws XmlPullParserException, IOException {
        parseUntilStartTag(parser);

        Collection<PreferenceHeader> preferenceHeaders = new LinkedList<>();
        int outerDepth = parser.getDepth();
        int tagType = parser.next();

        while (tagType != XmlPullParser.END_DOCUMENT &&
                (tagType != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
            if (tagType == XmlPullParser.END_TAG || tagType == XmlPullParser.TEXT) {
                continue;
            }

            if (parser.getName().equals(PREFERENCE_HEADER_TAG_NAME)) {
                preferenceHeaders.add(parsePreferenceHeader(context, parser));
            } else {
                skipCurrentTag(parser);
            }

            tagType = parser.next();
        }

        return preferenceHeaders;
    }

    /**
     * Parses until the start tag is found. If the start tag has an invalid name, a {@link
     * RuntimeException} is thrown.
     *
     * @param parser
     *         The parser, which should be used, as an instance of the class {@link
     *         XmlResourceParser}. The parser may not be null
     * @throws XmlPullParserException
     *         The exception, which is thrown, if an error occurs while parsing the XML file
     * @throws IOException
     *         The exception, which is thrown, if an error occurs while accessing the XML file
     */
    private static void parseUntilStartTag(@NonNull final XmlResourceParser parser)
            throws XmlPullParserException, IOException {
        int tagType = parser.next();

        while (tagType != XmlPullParser.END_DOCUMENT && tagType != XmlPullParser.START_TAG) {
            tagType = parser.next();
        }

        if (!parser.getName().equals(START_TAG_NAME)) {
            throw new RuntimeException(
                    "XML document must start with <preference-headers> tag; found" +
                            parser.getName() + " at " + parser.getPositionDescription());
        }
    }

    /**
     * Parses until the current tag is skipped.
     *
     * @param parser
     *         The parser, which should be used, as an instance of the class {@link
     *         XmlResourceParser}. The parser may not be null
     * @throws XmlPullParserException
     *         The exception, which is thrown, if an error occurs while parsing the XML file
     * @throws IOException
     *         The exception, which is thrown, if an error occurs while accessing the XML file
     */
    private static void skipCurrentTag(@NonNull final XmlPullParser parser)
            throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        int type = parser.next();

        while (type != XmlPullParser.END_DOCUMENT &&
                (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
            type = parser.next();
        }
    }

    /**
     * Parses and returns a single preference header by using a specific XML resource parser.
     *
     * @param context
     *         The context, which should be used for parsing, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param parser
     *         The parser, which should be used, as an instance of the type {@link XmlPullParser}.
     *         The parser may not be null
     * @return The preference header, which has been parsed, as an instance of the class {@link
     * PreferenceHeader}
     * @throws XmlPullParserException
     *         The exception, which is thrown, if an error occurs while parsing the XML file
     * @throws IOException
     *         The exception, which is thrown, if an error occurs while accessing the XML file
     */
    private static PreferenceHeader parsePreferenceHeader(@NonNull final Context context,
                                                          @NonNull final XmlResourceParser parser)
            throws XmlPullParserException, IOException {
        AttributeSet attributeSet = Xml.asAttributeSet(parser);
        TypedArray typedArray =
                context.getResources().obtainAttributes(attributeSet, R.styleable.PreferenceHeader);

        CharSequence title = parseTitle(context, typedArray);
        CharSequence summary = parseSummary(context, typedArray);
        CharSequence breadCrumbTitle = parseBreadCrumbTitle(context, typedArray);
        CharSequence breadCrumbShortTitle = parseBreadCrumbShortTitle(context, typedArray);
        Drawable icon = parseIcon(typedArray);
        String fragment = parseFragment(typedArray);
        Pair<Intent, Bundle> intentAndBundle = parseIntentAndBundle(context, parser, attributeSet);
        typedArray.recycle();

        PreferenceHeader preferenceHeader = new PreferenceHeader(title);
        preferenceHeader.setFragment(fragment);
        preferenceHeader.setSummary(summary);
        preferenceHeader.setBreadCrumbTitle(breadCrumbTitle);
        preferenceHeader.setBreadCrumbShortTitle(breadCrumbShortTitle);
        preferenceHeader.setIcon(icon);
        preferenceHeader.setIntent(intentAndBundle.first);
        preferenceHeader.setExtras(intentAndBundle.second);
        return preferenceHeader;
    }

    /**
     * Parses and returns the title of a preference header from a specific typed array.
     *
     * @param context
     *         The context, which should be used for parsing, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param typedArray
     *         The typed array, the title should be parsed from, as an instance of the class {@link
     *         TypedArray}. The typed array may not be null
     * @return The title, which has been parsed, as an instance of the class {@link CharSequence} or
     * null, if no title is defined by the given typed array
     */
    private static CharSequence parseTitle(@NonNull final Context context,
                                           @NonNull final TypedArray typedArray) {
        CharSequence title =
                parseCharSequence(context, typedArray, R.styleable.PreferenceHeader_android_title);

        if (title == null) {
            throw new RuntimeException("<header> tag must contain title attribute");
        }

        return title;
    }

    /**
     * Parses and returns the summary of a preference header from a specific typed array.
     *
     * @param context
     *         The context, which should be used for parsing, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param typedArray
     *         The typed array, the summary should be parsed from, as an instance of the class
     *         {@link TypedArray}. The typed array may not be null
     * @return The summary, which has been parsed, as an instance of the class {@link CharSequence}
     * or null, if no summary is defined by the given typed array
     */
    private static CharSequence parseSummary(@NonNull final Context context,
                                             @NonNull final TypedArray typedArray) {
        return parseCharSequence(context, typedArray, R.styleable.PreferenceHeader_android_summary);
    }

    /**
     * Parses and returns the bread crumb title of a preference header from a specific typed array.
     *
     * @param context
     *         The context, which should be used for parsing, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param typedArray
     *         The typed array, the bread crumb title should be parsed from, as an instance of the
     *         class {@link TypedArray}. The typed array may not be null
     * @return The title, which has been parsed, as an instance of the class {@link CharSequence} or
     * null, if no bread crumb title is defined by the given typed array
     */
    private static CharSequence parseBreadCrumbTitle(@NonNull final Context context,
                                                     @NonNull final TypedArray typedArray) {
        return parseCharSequence(context, typedArray,
                R.styleable.PreferenceHeader_android_breadCrumbTitle);
    }

    /**
     * Parses and returns the bread crumb short title of a preference header from a specific typed
     * array.
     *
     * @param context
     *         The context, which should be used for parsing, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param typedArray
     *         The typed array, the bread crumb short title should be parsed from, as an instance of
     *         the class {@link TypedArray}. The typed array may not be null
     * @return The title, which has been parsed, as an instance of the class {@link CharSequence} or
     * null, if no bread crumb short title is defined by the given typed array
     */
    private static CharSequence parseBreadCrumbShortTitle(@NonNull final Context context,
                                                          @NonNull final TypedArray typedArray) {
        return parseCharSequence(context, typedArray,
                R.styleable.PreferenceHeader_android_breadCrumbShortTitle);
    }

    /**
     * Parses and returns the char sequence, which is defined directly via a string or indirectly
     * via a resource id, at a specific index of a typed array.
     *
     * @param context
     *         The context, which should be used for parsing, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param typedArray
     *         The typed array, the char sequence should be parsed from, as an instance of the class
     *         {@link TypedArray}. The typed array may not be null
     * @param index
     *         The index, the char sequence is defined at, as an {@link Integer} value
     * @return The char sequence, which has been parsed, as an instance of the class {@link
     * CharSequence} or null, if no char sequence is defined at the given index
     */
    private static CharSequence parseCharSequence(@NonNull final Context context,
                                                  @NonNull final TypedArray typedArray,
                                                  final int index) {
        TypedValue typedValue = typedArray.peekValue(index);

        if (typedValue != null && typedValue.type == TypedValue.TYPE_STRING) {
            if (typedValue.resourceId != 0) {
                return context.getString(typedValue.resourceId);
            } else {
                return typedValue.string;
            }
        }

        return null;
    }

    /**
     * Parses and returns the icon of a preference header from a specific typed array.
     *
     * @param typedArray
     *         The typed array, the icon should be parsed from, as an instance of the class {@link
     *         TypedArray}. The typed array may not be null
     * @return The icon, which has been parsed, as an instance of the class {@link Drawable} or
     * null, if no icon is defined by the given typed array
     */
    private static Drawable parseIcon(@NonNull final TypedArray typedArray) {
        return typedArray.getDrawable(R.styleable.PreferenceHeader_android_icon);
    }

    /**
     * Parses and returns the fragment class name of a preference header from a specific typed
     * array.
     *
     * @param typedArray
     *         The typed array, the fragment class name should be parsed from, as an instance of the
     *         class {@link TypedArray}. The typed array may not be null
     * @return The fragment class name, which has been parsed, as a {@link String} or null, if no
     * fragment class name is defined by the given typed array
     */
    private static String parseFragment(@NonNull final TypedArray typedArray) {
        return typedArray.getString(R.styleable.PreferenceHeader_android_fragment);
    }

    /**
     * Parses and returns the intent and bundle of a preference header from a specific attribute set
     * by using a specific XML resource parser.
     *
     * @param context
     *         The context, which should be used for parsing, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param parser
     *         The parser, which should be used, as an instance of the type {@link XmlPullParser}.
     *         The parser may not be null
     * @param attributeSet
     *         The attribute set, the intent and bundle should be parsed from, as an instance of the
     *         type {@link AttributeSet}. The attribute set may not be null
     * @return The intent and bundle, which have been parsed, as an instance of the class {@link
     * Pair}
     * @throws XmlPullParserException
     *         The exception, which is thrown, if an error occurs while parsing the XML file
     * @throws IOException
     *         The exception, which is thrown, if an error occurs while accessing the XML file
     */
    private static Pair<Intent, Bundle> parseIntentAndBundle(@NonNull final Context context,
                                                             @NonNull final XmlPullParser parser,
                                                             @NonNull final AttributeSet attributeSet)
            throws XmlPullParserException, IOException {
        int innerDepth = parser.getDepth();
        int type = parser.next();
        Bundle bundle = new Bundle();
        Intent intent = null;

        while (type != XmlPullParser.END_DOCUMENT &&
                (type != XmlPullParser.END_TAG || parser.getDepth() > innerDepth)) {
            if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                continue;
            }

            if (parser.getName().equals(BUNDLE_TAG_NAME)) {
                context.getResources().parseBundleExtra(BUNDLE_TAG_NAME, attributeSet, bundle);
                skipCurrentTag(parser);
            } else if (parser.getName().equals(INTENT_TAG_NAME)) {
                intent = Intent.parseIntent(context.getResources(), parser, attributeSet);
            } else {
                skipCurrentTag(parser);
            }

            type = parser.next();
        }

        if (bundle.isEmpty()) {
            bundle = null;
        }

        return new Pair<>(intent, bundle);
    }

    /**
     * Parses and returns the preference headers, which are defined by a specific XML resource.
     *
     * @param context
     *         The context, which should be used to retrieve the XML resource, as an instance of the
     *         class {@link Context}. The context may not be null
     * @param resourceId
     *         The resource id of the XML file, which should be parsed, as an {@link Integer} value.
     *         The resource id must correspond to a valid XML resource
     * @return A collection, which contains the preference headers, which have been parsed from the
     * given XML resource, as an instance of the type {@link Collection}
     */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public static Collection<PreferenceHeader> fromResource(@NonNull final Context context,
                                                            final int resourceId) {
        XmlResourceParser parser = context.getResources().getXml(resourceId);

        try {
            return parsePreferenceHeaders(context, parser);
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException("Parsing preference headers failed", e);
        } finally {
            parser.close();
        }
    }

}