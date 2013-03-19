package pt.webdetails.cpf.repository;

import org.apache.commons.lang.StringUtils;
import pt.webdetails.cpf.session.IUserSession;

public abstract class BaseRepositoryAccess {
    
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
