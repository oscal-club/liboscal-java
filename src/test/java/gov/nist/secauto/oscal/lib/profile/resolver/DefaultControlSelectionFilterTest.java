/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.oscal.lib.profile.resolver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.oscal.lib.model.ProfileSelectControlById;
import gov.nist.secauto.oscal.lib.model.control.catalog.IControl;
import gov.nist.secauto.oscal.lib.model.control.profile.IProfileSelectControlById;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Collections;
import java.util.List;

class DefaultControlSelectionFilterTest {
  @RegisterExtension
  final JUnit5Mockery context = new JUnit5Mockery() {
    { // NOPMD - intentional
      setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }
  };

  @Mock
  private IProfileSelectControlById selectControlByIdA;
  @Mock
  private ProfileSelectControlById.Matching matchingA;
  @Mock
  private ProfileSelectControlById.Matching matchingB;
  @Mock
  private IProfileSelectControlById selectControlByIdB;

  @SuppressWarnings("null")
  private IControlSelectionFilter newEmptyFilter() {
    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(selectControlByIdA).getWithChildControls();
        will(returnValue("no"));
        allowing(selectControlByIdA).getWithIds();
        will(returnValue(Collections.emptyList()));
        allowing(selectControlByIdA).getMatching();
        will(returnValue(Collections.emptyList()));
      }
    });

    return new DefaultControlSelectionFilter(List.of(selectControlByIdA));
  }

  @SuppressWarnings("null")
  private IControlSelectionFilter newSingleSelectionFilter() {
    final List<String> withIds = List.of("test1", "test2");
    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(selectControlByIdA).getWithChildControls();
        will(returnValue("no"));
        allowing(selectControlByIdA).getWithIds();
        will(returnValue(withIds));
        allowing(selectControlByIdA).getMatching();
        will(returnValue(List.of(matchingA)));
        allowing(matchingA).getPattern();
        will(returnValue("other*"));
      }
    });

    return new DefaultControlSelectionFilter(List.of(selectControlByIdA));
  }

  @SuppressWarnings("null")
  private IControlSelectionFilter newSingleSelectionWithChildFilter() {
    final List<String> withIds = List.of("test1", "test2");
    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(selectControlByIdA).getWithChildControls();
        will(returnValue("yes"));
        allowing(selectControlByIdA).getWithIds();
        will(returnValue(withIds));
        allowing(selectControlByIdA).getMatching();
        will(returnValue(List.of(matchingA)));
        allowing(matchingA).getPattern();
        will(returnValue("other*"));
      }
    });

    return new DefaultControlSelectionFilter(List.of(selectControlByIdA));
  }

  @SuppressWarnings("null")
  private IControlSelectionFilter newMultipleSelectionFilter() {
    final List<String> withIdsA = List.of("test1", "test2", "example1");
    final List<String> withIdsB = List.of("test3", "test4");
    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(selectControlByIdA).getWithChildControls();
        will(returnValue("yes"));
        allowing(selectControlByIdA).getWithIds();
        will(returnValue(withIdsA));
        allowing(selectControlByIdA).getMatching();
        will(returnValue(List.of(matchingA)));
        allowing(matchingA).getPattern();
        will(returnValue("other*"));

        allowing(selectControlByIdB).getWithChildControls();
        will(returnValue("no"));
        allowing(selectControlByIdB).getWithIds();
        will(returnValue(withIdsB));
        allowing(selectControlByIdB).getMatching();
        will(returnValue(List.of(matchingB)));
        allowing(matchingB).getPattern();
        will(returnValue("example1*"));
      }
    });

    return new DefaultControlSelectionFilter(List.of(selectControlByIdA, selectControlByIdB));
  }

  /**
   * Test the filtering of an empty set of match criteria
   */
  @Test
  void testEmpty() {
    @SuppressWarnings("null")
    final IControl control1 = context.mock(IControl.class);
    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(control1).getId();
        will(returnValue("test"));
      }
    });

    IControlSelectionFilter filter = newEmptyFilter();
    Pair<@NotNull Boolean, @NotNull Boolean> pair = filter.apply(control1);
    assertFalse(pair.getLeft());
    assertFalse(pair.getRight());
  }

  /**
   * Test the filtering of a single match criteria using "with-ids".
   */
  @Test
  void testSingleSelectionWithIdsFilter() {
    @SuppressWarnings("null")
    final IControl control1 = context.mock(IControl.class, "control1");
    @SuppressWarnings("null")
    final IControl control2 = context.mock(IControl.class, "control2");
    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(control1).getId();
        will(returnValue("test"));
        allowing(control2).getId();
        will(returnValue("test2"));
      }
    });

    IControlSelectionFilter filter = newSingleSelectionFilter();
    Pair<@NotNull Boolean, @NotNull Boolean> pair = filter.apply(control1);
    assertFalse(pair.getLeft());
    assertFalse(pair.getRight());

    pair = filter.apply(control2);
    assertTrue(pair.getLeft());
    assertFalse(pair.getRight());
  }

  /**
   * Test the filtering of a single match criteria using "with-ids" and "with-child=yes".
   */
  @Test
  void testSingleSelectionWithIdsWithChildFilter() {
    @SuppressWarnings("null")
    final IControl control1 = context.mock(IControl.class, "control1");
    @SuppressWarnings("null")
    final IControl control2 = context.mock(IControl.class, "control2");
    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(control1).getId();
        will(returnValue("test"));
        allowing(control2).getId();
        will(returnValue("test2"));
      }
    });

    IControlSelectionFilter filter = newSingleSelectionWithChildFilter();
    Pair<@NotNull Boolean, @NotNull Boolean> pair = filter.apply(control1);
    assertFalse(pair.getLeft());
    assertFalse(pair.getRight());

    pair = filter.apply(control2);
    assertTrue(pair.getLeft());
    assertTrue(pair.getRight());
  }

  /**
   * Test the filtering of multiple match criteria.
   */
  @Test
  void testMultipleSelectionFilter() {
    @SuppressWarnings("null")
    final IControl control1 = context.mock(IControl.class, "control1");
    @SuppressWarnings("null")
    final IControl control2 = context.mock(IControl.class, "control2");
    @SuppressWarnings("null")
    final IControl control3 = context.mock(IControl.class, "control3");
    @SuppressWarnings("null")
    final IControl control4 = context.mock(IControl.class, "control4");
    @SuppressWarnings("null")
    final IControl control5 = context.mock(IControl.class, "control5");
    @SuppressWarnings("null")
    final IControl control6 = context.mock(IControl.class, "control6");
    @SuppressWarnings("null")
    final IControl control7 = context.mock(IControl.class, "control7");
    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(control1).getId();
        will(returnValue("test"));
        allowing(control2).getId();
        will(returnValue("test2"));
        allowing(control3).getId();
        will(returnValue("test4"));
        allowing(control4).getId();
        will(returnValue("other1"));
        allowing(control5).getId();
        will(returnValue("other"));
        allowing(control6).getId();
        will(returnValue("example1"));
        allowing(control7).getId();
        will(returnValue("example11"));
      }
    });

    IControlSelectionFilter filter = newMultipleSelectionFilter();
    Pair<@NotNull Boolean, @NotNull Boolean> pair = filter.apply(control1);
    assertFalse(pair.getLeft());
    assertFalse(pair.getRight());

    pair = filter.apply(control2);
    assertTrue(pair.getLeft());
    assertTrue(pair.getRight());

    pair = filter.apply(control3);
    assertTrue(pair.getLeft());
    assertFalse(pair.getRight());

    // test match patterns
    pair = filter.apply(control4);
    assertTrue(pair.getLeft());
    assertTrue(pair.getRight());

    pair = filter.apply(control5);
    assertTrue(pair.getLeft());
    assertTrue(pair.getRight());

    pair = filter.apply(control6);
    assertTrue(pair.getLeft());
    assertTrue(pair.getRight());

    pair = filter.apply(control7);
    assertTrue(pair.getLeft());
    assertFalse(pair.getRight());
  }
}
