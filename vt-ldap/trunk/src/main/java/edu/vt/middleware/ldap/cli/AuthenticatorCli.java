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
package edu.vt.middleware.ldap.cli;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Map;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapConnectionConfig;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.auth.AuthenticationRequest;
import edu.vt.middleware.ldap.auth.Authenticator;
import edu.vt.middleware.ldap.auth.SearchDnResolver;
import edu.vt.middleware.ldap.dsml.Dsmlv1Writer;
import edu.vt.middleware.ldap.ldif.LdifWriter;
import edu.vt.middleware.ldap.props.AuthenticationRequestPropertySource;
import edu.vt.middleware.ldap.props.AuthenticatorPropertySource;
import edu.vt.middleware.ldap.props.LdapConnectionConfigPropertySource;
import edu.vt.middleware.ldap.props.PropertySource.PropertyDomain;
import edu.vt.middleware.ldap.props.SearchDnResolverPropertySource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Command line interface for {@link Authenticator}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AuthenticatorCli extends AbstractCli
{

  /** option for dsmlv1 output. */
  private static final String OPT_DSMLV1 = "dsmlv1";

  /** name of operation provided by this class. */
  private static final String COMMAND_NAME = "ldapauth";


  /**
   * CLI entry point method.
   *
   * @param  args  command line arguments.
   */
  public static void main(final String[] args)
  {
    new AuthenticatorCli().performAction(args);
  }


  /** {@inheritDoc} */
  protected void initOptions()
  {
    this.options.addOption(
      new Option(OPT_DSMLV1, false, "output results in DSML v1"));
    final Map<String, String> desc = this.getArgDesc(
      LdapConnectionConfig.class,
      Authenticator.class,
      SearchDnResolver.class,
      AuthenticationRequest.class);
    for (String s : LdapConnectionConfigPropertySource.getProperties()) {
      this.options.addOption(new Option(s, true, desc.get(s)));
    }
    for (String s : AuthenticatorPropertySource.getProperties()) {
      this.options.addOption(new Option(s, true, desc.get(s)));
    }
    for (String s : SearchDnResolverPropertySource.getProperties()) {
      this.options.addOption(new Option(s, true, desc.get(s)));
    }
    for (String s : AuthenticationRequestPropertySource.getProperties()) {
      this.options.addOption(new Option(s, true, desc.get(s)));
    }
    super.initOptions();
  }


  /**
   * Initialize an Authenticator with command line options.
   *
   * @param  line  parsed command line arguments
   *
   * @return  authenticator that has been initialized
   *
   * @throws  Exception  if an authenticator cannot be created
   */
  protected Authenticator initAuthenticator(final CommandLine line)
    throws Exception
  {
    final AuthenticatorPropertySource aSource =
      new AuthenticatorPropertySource(
        this.getPropertiesFromOptions(PropertyDomain.AUTH.value(), line));
    final Authenticator auth = aSource.get();
    return auth;
  }


  /**
   * Initialize an authentication request with command line options.
   *
   * @param  line  parsed command line arguments
   *
   * @return  authentication request that has been initialized
   *
   * @throws  Exception  if an authentication request cannot be created
   */
  protected AuthenticationRequest initAuthenticationRequest(
    final CommandLine line)
    throws Exception
  {
    final AuthenticationRequestPropertySource arSource =
      new AuthenticationRequestPropertySource(
        this.getPropertiesFromOptions(PropertyDomain.AUTH.value(), line));
    final AuthenticationRequest request = arSource.get();
    if (request.getUser() == null) {
      // prompt for a user name
      final String user = System.console().readLine("[Enter user name]: ");
      request.setUser(user);
    }

    if (request.getCredential() == null) {
      // prompt the user to enter a password
      final char[] pass = System.console().readPassword(
        "[Enter password for %s]: ", request.getUser());
      request.setCredential(new Credential(pass));
    }

    return request;
  }


  /** {@inheritDoc} */
  protected void dispatch(final CommandLine line)
    throws Exception
  {
    if (line.hasOption(OPT_DSMLV1)) {
      this.outputDsmlv1 = true;
    }
    if (line.hasOption(OPT_HELP)) {
      this.printHelp();
    } else {
      this.authenticate(
        this.initAuthenticator(line),
        this.initAuthenticationRequest(line));
    }
  }


  /**
   * Executes the authentication operation.
   *
   * @param  auth  authenticator
   * @param  request  authentication request
   *
   * @throws  Exception  on any LDAP error
   */
  protected void authenticate(
    final Authenticator auth,
    final AuthenticationRequest request)
    throws Exception
  {
    // by default return all attributes
    if (request.getReturnAttributes() != null &&
        request.getReturnAttributes().length == 0) {
      request.setReturnAttributes(null);
    }
    final LdapEntry entry = auth.authenticate(request).getResult();
    if (entry != null) {
      if (this.outputDsmlv1) {
        final Dsmlv1Writer writer = new Dsmlv1Writer(
          new BufferedWriter(new OutputStreamWriter(System.out)));
        writer.write(new LdapResult(entry));
      } else {
        final LdifWriter writer = new LdifWriter(
          new BufferedWriter(new OutputStreamWriter(System.out)));
        writer.write(new LdapResult(entry));
      }
    }
  }


  /** {@inheritDoc} */
  protected String getCommandName()
  {
    return COMMAND_NAME;
  }
}
