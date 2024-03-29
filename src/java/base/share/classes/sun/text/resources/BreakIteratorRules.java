/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.text.resources;

import java.util.ListResourceBundle;

/**
 * Default break-iterator rules.  These rules are more or less general for
 * all locales, although there are probably a few we're missing.  The
 * behavior currently mimics the behavior of BreakIterator in JDK 1.2.
 * There are known deficiencies in this behavior, including the fact that
 * the logic for handling CJK characters works for Japanese but not for
 * Chinese, and that we don't currently have an appropriate locale for
 * Thai.  The resources will eventually be updated to fix these problems.
 */

/*
 * Since JDK 1.5.0, this file no longer goes to runtime and is used at J2SE
 * build phase in order to create [Character|Word|Line|Sentence]BreakIteratorData
 * files which are used on runtime instead.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 20/4/2023 
 */

public class BreakIteratorRules extends ListResourceBundle {
    protected final Object[][] getContents() {
        return new Object[][] {
            // default rules for finding word boundaries
            { "WordBreakRules",
              // ignore non-spacing marks, enclosing marks, and format characters,
              // all of which should not influence the algorithm
              //"<ignore>=[:Mn::Me::Cf:];"
              "<ignore>=[:Cf:];"

              + "<enclosing>=[:Mn::Me:];"

              // Hindi phrase separator, kanji, katakana, hiragana, CJK diacriticals,
              // other letters, and digits
              + "<danda>=[\u0964\u0965];"
              + "<kanji>=[\u3005\u4e00-\u9fa5\uf900-\ufa2d];"
              + "<kata>=[\u30a1-\u30fa\u30fd\u30fe];"
              + "<hira>=[\u3041-\u3094\u309d\u309e];"
              + "<cjk-diacrit>=[\u3099-\u309c\u30fb\u30fc];"
              + "<letter-base>=[:L::Mc:^[<kanji><kata><hira><cjk-diacrit>]];"
              + "<let>=(<letter-base><enclosing>*);"
              + "<digit-base>=[:N:];"
              + "<dgt>=(<digit-base><enclosing>*);"

              // punctuation that can occur in the middle of a word: currently
              // dashes, apostrophes, quotation marks, and periods
              + "<mid-word>=[:Pd::Pc:\u00ad\u2027\\\"\\\'\\.];"

              // punctuation that can occur in the middle of a number: currently
              // apostrophes, qoutation marks, periods, commas, and the Arabic
              // decimal point
              + "<mid-num>=[\\\"\\\'\\,\u066b\\.];"

              // punctuation that can occur at the beginning of a number: currently
              // the period, the number sign, and all currency symbols except the cents sign
              + "<pre-num>=[:Sc:\\#\\.^\u00a2];"

              // punctuation that can occur at the end of a number: currently
              // the percent, per-thousand, per-ten-thousand, and Arabic percent
              // signs, the cents sign, and the ampersand
              + "<post-num>=[\\%\\&\u00a2\u066a\u2030\u2031];"

              // line separators: currently LF, FF, PS, and LS
              + "<ls>=[\n\u000c\u2028\u2029];"

              // whitespace: all space separators and the tab character
              + "<ws-base>=[:Zs:\t];"
              + "<ws>=(<ws-base><enclosing>*);"

              // a word is a sequence of letters that may contain internal
              // punctuation, as long as it begins and ends with a letter and
              // never contains two punctuation marks in a row
              + "<word>=((<let><let>*(<mid-word><let><let>*)*){<danda>});"

              // a number is a sequence of digits that may contain internal
              // punctuation, as long as it begins and ends with a digit and
              // never contains two punctuation marks in a row.
              + "<number>=(<dgt><dgt>*(<mid-num><dgt><dgt>*)*);"

              // break after every character, with the following exceptions
              // (this will cause punctuation marks that aren't considered
              // part of words or numbers to be treated as words unto themselves)
              + ".;"

              // keep together any sequence of contiguous words and numbers
              // (including just one of either), plus an optional trailing
              // number-suffix character
              + "{<word>}(<number><word>)*{<number>{<post-num>}};"

              // keep together and sequence of contiguous words and numbers
              // that starts with a number-prefix character and a number,
              // and may end with a number-suffix character
              + "<pre-num>(<number><word>)*{<number>{<post-num>}};"

              // keep together runs of whitespace (optionally with a single trailing
              // line separator or CRLF sequence)
              + "<ws>*{\r}{<ls>};"

              // keep together runs of Katakana and CJK diacritical marks
              + "[<kata><cjk-diacrit>]*;"

              // keep together runs of Hiragana and CJK diacritical marks
              + "[<hira><cjk-diacrit>]*;"

              // keep together runs of Kanji
              + "<kanji>*;"

              // keep together anything else and an enclosing mark
              + "<base>=[^<enclosing>^[:Cc::Cf::Zl::Zp:]];"
              + "<base><enclosing><enclosing>*;"
            },

            // default rules for determining legal line-breaking positions
            { "LineBreakRules",
              // characters that always cause a break: ETX, tab, LF, FF, LS, and PS
              "<break>=[\u0003\t\n\f\u2028\u2029];"

              // ignore format characters and control characters EXCEPT for breaking chars
              + "<ignore>=[:Cf:[:Cc:^[<break>\r]]];"

              // enclosing marks
              + "<enclosing>=[:Mn::Me:];"

              // Hindi phrase separators
              + "<danda>=[\u0964\u0965];"

              // characters that always prevent a break: the non-breaking space
              // and similar characters
              + "<glue>=[\u00a0\u0f0c\u2007\u2011\u202f\ufeff];"

              // whitespace: space separators and control characters, except for
              // CR and the other characters mentioned above
              + "<space>=[:Zs::Cc:^[<glue><break>\r]];"

              // dashes: dash punctuation and the discretionary hyphen, except for
              // non-breaking hyphens
              + "<dash>=[:Pd:\u00ad^<glue>];"

              // characters that stick to a word if they precede it: currency symbols
              // (except the cents sign) and starting punctuation
              + "<pre-word>=[:Sc::Ps::Pi:^[\u00a2]\\\"\\\'];"

              // characters that stick to a word if they follow it: ending punctuation,
              // other punctuation that usually occurs at the end of a sentence,
              // small Kana characters, some CJK diacritics, etc.
              + "<post-word>=[\\\":Pe::Pf:\\!\\%\\.\\,\\:\\;\\?\u00a2\u00b0\u066a\u2030-\u2034\u2103"
              + "\u2105\u2109\u3001\u3002\u3005\u3041\u3043\u3045\u3047\u3049\u3063"
              + "\u3083\u3085\u3087\u308e\u3099-\u309e\u30a1\u30a3\u30a5\u30a7\u30a9"
              + "\u30c3\u30e3\u30e5\u30e7\u30ee\u30f5\u30f6\u30fc-\u30fe\uff01\uff05"
              + "\uff0c\uff0e\uff1a\uff1b\uff1f];"

              // Kanji: actually includes Kanji,Kana and Hangul syllables,
              // except for small Kana and CJK diacritics
              + "<kanji>=[\u4e00-\u9fa5\uac00-\ud7a3\uf900-\ufa2d\ufa30-\ufa6a\u3041-\u3094\u30a1-\u30fa^[<post-word><ignore>]];"

              // digits
              + "<digit>=[:Nd::No:];"

              // punctuation that can occur in the middle of a number: periods and commas
              + "<mid-num>=[\\.\\,];"

              // everything not mentioned above
              + "<char>=[^[<break><space><dash><kanji><glue><ignore><pre-word><post-word><mid-num>\r<danda>]];"

              // a "number" is a run of prefix characters and dashes, followed by one or
              // more digits with isolated number-punctuation characters interspersed
              + "<number>=([<pre-word><dash>]*<digit><digit>*(<mid-num><digit><digit>*)*);"

              // the basic core of a word can be either a "number" as defined above, a single
              // "Kanji" character, or a run of any number of not-explicitly-mentioned
              // characters (this includes Latin letters)
              + "<word-core>=(<char>*|<kanji>|<number>);"

              // a word may end with an optional suffix that be either a run of one or
              // more dashes or a run of word-suffix characters
              + "<word-suffix>=((<dash><dash>*|<post-word>*));"

              // a word, thus, is an optional run of word-prefix characters, followed by
              // a word core and a word suffix (the syntax of <word-core> and <word-suffix>
              // actually allows either of them to match the empty string, putting a break
              // between things like ")(" or "aaa(aaa"
              + "<word>=(<pre-word>*<word-core><word-suffix>);"

              + "<hack1>=[\\(];"
              + "<hack2>=[\\)];"
              + "<hack3>=[\\$\\'];"

              // finally, the rule that does the work: Keep together any run of words that
              // are joined by runs of one of more non-spacing mark.  Also keep a trailing
              // line-break character or CRLF combination with the word.  (line separators
              // "win" over nbsp's)
              + "<word>(((<space>*<glue><glue>*{<space>})|<hack3>)<word>)*<space>*{<enclosing>*}{<hack1><hack2><post-word>*}{<enclosing>*}{\r}{<break>};"
              + "\r<break>;"
            },

            // default rules for finding sentence boundaries
            { "SentenceBreakRules",
              // ignore non-spacing marks, enclosing marks, and format characters
              "<ignore>=[:Mn::Me::Cf:];"

              // letters
              + "<letter>=[:L:];"

              // lowercase letters
              + "<lc>=[:Ll:];"

              // uppercase letters
              + "<uc>=[:Lu:];"

              // NOT lowercase letters
              + "<notlc>=[<letter>^<lc>];"

              // whitespace (line separators are treated as whitespace)
              + "<space>=[\t\r\f\n\u2028:Zs:];"

              // punctuation which may occur at the beginning of a sentence: "starting
              // punctuation" and quotation marks
              + "<start-punctuation>=[:Ps::Pi:\\\"\\\'];"

              // punctuation which may occur at the end of a sentence: "ending punctuation"
              // and quotation marks
              + "<end>=[:Pe::Pf:\\\"\\\'];"

              // digits
              + "<digit>=[:N:];"

              // characters that unambiguously signal the end of a sentence
              + "<term>=[\\!\\?\u3002\uff01\uff1f];"

              // periods, which MAY signal the end of a sentence
              + "<period>=[\\.\uff0e];"

              // comma, which may not occur at the start of a sentence
              + "<comma>=[\\,];"

              // characters that may occur at the beginning of a sentence: basically anything
              // not mentioned above (letters and digits are specifically excluded)
              + "<sent-start>=[^[:L:<space><start-punctuation><end><digit><term><period><comma>\u2029<ignore>]];"

              // Hindi phrase separator
              + "<danda>=[\u0964\u0965];"

              // always break sentences after paragraph separators
              + ".*?{\u2029};"

              // always break after a danda, if it's followed by whitespace
              + ".*?<danda><space>*;"

              // if you see a period, skip over additional periods and ending punctuation
              // and if the next character is a paragraph separator, break after the
              // paragraph separator
              //+ ".*?<period>[<period><end>]*<space>*\u2029;"
              //+ ".*?[<period><end>]*<space>*\u2029;"

              // if you see a period, skip over additional periods and ending punctuation,
              // followed by optional whitespace, followed by optional starting punctuation,
              // and if the next character is something that can start a sentence
              // (basically, a capital letter), then put the sentence break between the
              // whitespace and the opening punctuation
              + ".*?<period>[<period><end>]*<space><space>*/<notlc>;"
              + ".*?<period>[<period><end>]*<space>*/[<start-punctuation><sent-start>][<start-punctuation><sent-start>]*<letter>;"

              // if you see a sentence-terminating character, skip over any additional
              // terminators, periods, or ending punctuation, followed by any whitespace,
              // followed by a SINGLE optional paragraph separator, and put the break there
              + ".*?<term>[<term><period><end>]*<space>*{\u2029};"

              // The following rules are here to aid in backwards iteration.  The automatically
              // generated backwards state table will rewind to the beginning of the
              // paragraph all the time (or all the way to the beginning of the document
              // if the document doesn't use the Unicode PS character) because the only
              // unambiguous character pairs are those involving paragraph separators.
              // These specify a few more unambiguous breaking situations.

              // if you see a sentence-starting character, followed by starting punctuation
              // (remember, we're iterating backwards), followed by an optional run of
              // whitespace, followed by an optional run of ending punctuation, followed
              // by a period, this is a safe place to turn around
              + "!<sent-start><start-punctuation>*<space>*<end>*<period>;"

              // if you see a letter or a digit, followed by an optional run of
              // starting punctuation, followed by an optional run of whitespace,
              // followed by an optional run of ending punctuation, followed by
              // a sentence terminator, this is a safe place to turn around
              + "![<sent-start><lc><digit>]<start-punctuation>*<space>*<end>*<term>;"
            }
        };
    }
}
