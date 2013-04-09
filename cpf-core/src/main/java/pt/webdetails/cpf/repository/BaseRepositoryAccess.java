/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.repository;

import org.apache.commons.lang.StringUtils;
import pt.webdetails.cpf.session.IUserSession;

public abstract class BaseRepositoryAccess implements IRepositoryAccess {
    
    protected IUserSession userSession;

    public enum FileAccess {//TODO:use masks?

        READ,
        EDIT,
        EXECUTE,
        DELETE,
        CREATE,
        NONE;

        public static FileAccess parse(String fileAccess) {
            try {
                return FileAccess.valueOf(StringUtils.upperCase(fileAccess));
            } catch (Exception e) {
                return null;
            }
        }
    }

    public enum SaveFileStatus {
        //TODO: do we need more than this? use bool?

        OK,
        FAIL
    }
}
