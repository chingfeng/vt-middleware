/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.control;

/**
 * Request control for PagedResults. See RFC 2696.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PagedResultsControl extends AbstractControl
{

  /** paged results size. */
  private int resultSize;


  /**
   * Default constructor.
   */
  public PagedResultsControl() {}


  /**
   * Creates a new paged results.
   *
   * @param  size  paged results size
   */
  public PagedResultsControl(final int size)
  {
    setSize(size);
  }


  /**
   * Creates a new paged results.
   *
   * @param  size  paged results size
   * @param  critical  whether this control is critical
   */
  public PagedResultsControl(final int size, final boolean critical)
  {
    setSize(size);
    setCriticality(critical);
  }


  /**
   * Returns the paged results size.
   *
   * @return  paged results size
   */
  public int getSize()
  {
    return resultSize;
  }


  /**
   * Sets the paged results size.
   *
   * @param  size  paged results size
   */
  public void setSize(final int size)
  {
    resultSize = size;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::criticality=%s, size=%s]",
        getClass().getName(),
        hashCode(),
        criticality,
        resultSize);
  }
}
