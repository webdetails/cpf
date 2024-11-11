/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


/*
 * Helper for audit logs
 */

package pt.webdetails.cpf.audit;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IParameterProvider;


import java.util.UUID;

import org.pentaho.platform.api.engine.ILogger;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.audit.AuditHelper;
import org.pentaho.platform.engine.core.audit.MDCUtil;
import org.pentaho.platform.engine.core.audit.MessageTypes;

public class CpfAuditHelper {


  private static final Log log = LogFactory.getLog( CpfAuditHelper.class );

  /**
   * Start Audit Event
   *
   * @param processId     Id for the audit process (usually the plugin name)
   * @param actionName    Name of the action
   * @param objectName    Object of the action
   * @param userSession   Pentaho User Session
   * @param logger        Logger object
   * @param requestParams parameters associated to the request
   * @return UUID of start event
   */
  public static UUID startAudit(
          String processId,
          String actionName,
          String objectName,
          IPentahoSession userSession,
          ILogger logger,
          IParameterProvider requestParams ) {
    UUID uuid = UUID.randomUUID();

    MDCUtil.setInstanceId( uuid.toString() );

    StringBuilder sb = new StringBuilder();
    if ( requestParams != null ) {
      @SuppressWarnings( "unchecked" )
      Iterator<String> iter = requestParams.getParameterNames();
      while ( iter.hasNext() ) {
        String paramName = iter.next();
        sb.append( paramName ).append( "=" ).
                append(requestParams.getStringParameter( paramName, "novalue" ) ).append( ";" );
      }
    }

    try {
      AuditHelper.audit( userSession.getId(), userSession.getName(), actionName, objectName,
              processId, MessageTypes.INSTANCE_START, uuid.toString(), sb.toString(), 0, logger );
    } catch ( Exception e ) {
      log.warn( "Exception while writing to audit log. Returning null as audit event ID but"
              + " will continue execution ", e );
      return null;
    }

    return uuid;
  }

  /**
   * End Audit Event
   *
   * @param processId   Id for the audit process (usually the plugin name)
   * @param actionName  Name of the action
   * @param objectName  Object of the action
   * @param userSession Pentaho User Session
   * @param logger      Logger object
   * @param start       Start time in Millis Seconds
   * @param uuid        UUID of start event
   * @param end         End time in Millis Seconds
   */
  public static void endAudit( String processId, String actionName, String objectName, IPentahoSession userSession,
                               ILogger logger, long start, UUID uuid, long end ) {
    try {
      AuditHelper.audit(userSession.getId(), userSession.getName(), actionName, objectName, processId,
              MessageTypes.INSTANCE_END, uuid.toString(), "", ( (float) ( end - start ) / 1000 ), logger );
    } catch ( Exception e ) {
      log.warn( "Exception while writing to audit log. Returning null as audit event ID but"
              + " will continue execution ", e );
    }
  }

}

