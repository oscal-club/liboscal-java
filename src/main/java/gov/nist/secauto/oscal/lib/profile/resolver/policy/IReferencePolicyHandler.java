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

package gov.nist.secauto.oscal.lib.profile.resolver.policy;

import gov.nist.secauto.oscal.lib.profile.resolver.EntityItem;
import gov.nist.secauto.oscal.lib.profile.resolver.EntityItem.ItemType;
import gov.nist.secauto.oscal.lib.profile.resolver.Index;
import gov.nist.secauto.oscal.lib.profile.resolver.policy.IIdentifierParser.Match;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface IReferencePolicyHandler<TYPE> {
  @NotNull
  public static IReferencePolicyHandler<?> IGNORE_INDEX_MISS_POLICY = new AbstractIndexMissPolicyHandler<>() {

    @Override
    public boolean handleIndexMiss(@NotNull Object type, @NotNull Set<ItemType> itemTypes,
        @NotNull Match match, @NotNull Index index) {
      // do nothing
      return true;
    }
  };

  public static IReferencePolicyHandler<?> INCREMENT_COUNT_INDEX_HIT_POLICY = new IReferencePolicyHandler<>() {

    @Override
    public boolean handleIndexHit(EntityItem item, @NotNull Object type, @NotNull Index index) {
      item.incrementReferenceCount();
      return true;
    }
  };

  @SuppressWarnings("unchecked")
  @NotNull
  public static <T> IReferencePolicyHandler<T> incrementCountIndexHitPolicy() {
    return (@NotNull IReferencePolicyHandler<T>) INCREMENT_COUNT_INDEX_HIT_POLICY;
  }

  default boolean handleIdentifierNonMatch(@NotNull TYPE type, @NotNull Match match,
      @NotNull Index index) {
    return false;
  }

  default boolean handleIndexMiss(@NotNull TYPE type, @NotNull Set<EntityItem.ItemType> itemTypes,
      @NotNull Match match, @NotNull Index index) {
    return false;
  }

  default boolean handleIndexHit(EntityItem item, @NotNull TYPE type, @NotNull Index index) {
    return false;
  }
}
