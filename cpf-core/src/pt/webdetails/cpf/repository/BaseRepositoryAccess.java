/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.repository;

import pt.webdetails.cpf.session.IUserSession;

/**
 * This existed basically to hold enums, which didn't make sense.
 * Sorry about breaking your code.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;--tgf
 * @deprecated
 */
public abstract class BaseRepositoryAccess implements IRepositoryAccess {

    protected IUserSession userSession;//TODO: we don't really need this here

    
}
