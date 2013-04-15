/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.scripts;

import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.engine.IUserDetailsRoleListService;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.StandaloneSession;
import org.pentaho.platform.engine.core.system.UserSession;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.pentaho.platform.repository.hibernate.HibernateUtil;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;
import pt.webdetails.cpf.Router;
import pt.webdetails.cpf.datasources.DatasourceFactory;
import pt.webdetails.cpf.repository.RepositoryAccess;
import pt.webdetails.cpf.repository.RepositoryAccess.FileAccess;
import pt.webdetails.cpf.persistence.PersistenceEngine;
import pt.webdetails.cpk.CpkContentGenerator;

/**
 *
 * @author pdpi
 */
public class GlobalScope extends ImporterTopLevel {

    private static final long serialVersionUID = -3528272077278611074L;
    private static final int START_LINE = 1;
    protected static final Log logger = LogFactory.getLog(GlobalScope.class);
    private static GlobalScope _instance;
    private static ContextFactory contextFactory;
    private final static String systemPath = "/system/cdv/js/";
    private final static String testPath = "/cdv/tests/";
    private static IPentahoSession session;

    public static synchronized GlobalScope getInstance() {
        if (_instance == null) {
            _instance = new GlobalScope();
        }
        return _instance;
    }

    public static synchronized GlobalScope reset() {
        _instance = new GlobalScope();
        return _instance;
    }

    public GlobalScope() {
        super();
        init();
    }

    private void init() {
        Context cx = getContextFactory().enterContext();
        try {
            cx.initStandardObjects(this);
            String[] names = {
                "registerHandler", "callWithDefaultSession", "print", "lib", "load", "loadTests", "getPluginSetting"};
            defineFunctionProperties(names, GlobalScope.class,
                    ScriptableObject.DONTENUM);
            /* Object wrappedEventManager = Context.javaToJS(EventManager.getInstance(), this);
            ScriptableObject.putProperty(this, "eventManager", wrappedEventManager); */
            Object wrappedPersistence = Context.javaToJS(PersistenceEngine.getInstance(), this);
            ScriptableObject.putProperty(this, "persistenceEngine", wrappedPersistence);
            Object wrappedFactory = Context.javaToJS(new DatasourceFactory(), this);
            ScriptableObject.putProperty(this, "datasourceFactory", wrappedFactory);
        } finally {
            Context.exit();
        }

    }

    public static ContextFactory getContextFactory() {
        if (contextFactory == null) {
            contextFactory = new ContextFactory();
        }
        return contextFactory;
    }

    public static Object registerHandler(Context cx, Scriptable thisObj,
            Object[] args, Function funObj) {

        String method = args[0].toString();
        String path = args[1].toString();
        Function handler = (Function) args[2];
        try {
            Router.getBaseRouter().registerHandler(Router.HttpMethod.valueOf(method), path, handler);
            //BaseScope scope = (BaseScope) thisObj;
            //cx.evaluateReader(scope, new FileReader(scope.systemPath + "/" + file), file, 1, null);
        } catch (Exception e) {
            return Context.toBoolean(false);
        }
        return Context.toBoolean(true);
    }
    /*
     public static Object setTimeout(Context cx, Scriptable thisObj,
     Object[] args, Function funObj) {
    
     String method = args[0].toString();
     String path = args[1].toString();
     Function handler = (Function) args[2];
     try {
     Router.getBaseRouter().registerHandler(Router.HttpMethod.valueOf(method), path, handler);
     //BaseScope scope = (BaseScope) thisObj;
     //cx.evaluateReader(scope, new FileReader(scope.systemPath + "/" + file), file, 1, null);
     } catch (Exception e) {
     return Context.toBoolean(false);
     }
    
     public static Object clearTimeout(Context cx, Scriptable thisObj,
     Object[] args, Function funObj) {
    
     String method = args[0].toString();
     String path = args[1].toString();
     Function handler = (Function) args[2];
     try {
     Router.getBaseRouter().registerHandler(Router.HttpMethod.valueOf(method), path, handler);
     //BaseScope scope = (BaseScope) thisObj;
     //cx.evaluateReader(scope, new FileReader(scope.systemPath + "/" + file), file, 1, null);
     } catch (Exception e) {
     return Context.toBoolean(false);
     }
     */

//    public static InputStream readFile(String path) throws FileNotFoundException {
//        final ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, PentahoSessionHolder.getSession());
//        // Get the paths ot the necessary files: dependencies and the main script.
//        return solutionRepository.getResourceInputStream(path, false, 0);
//
//    }
    public static Object loadTests(Context cx, Scriptable thisObj,
            Object[] args, Function funObj) {
        /* Get the repository, and get a listing of all the files in the test dir from it*/
//        final ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, PentahoSessionHolder.getSession());
//        ISolutionFile dir = solutionRepository.getSolutionFile(testPath, ISolutionRepository.ACTION_EXECUTE);
        RepositoryAccess repository = RepositoryAccess.getRepository();
        ISolutionFile testDir = repository.getSolutionFile(testPath, FileAccess.EXECUTE);

        if (testDir != null) {

            ISolutionFile files[] = testDir.listFiles();

            /* For each test file, read it into a stream and execute it. */
            for (ISolutionFile file : files) {
                if (file.isDirectory()) {
                    continue;
                }
                String path = file.getFullPath();
                // workaround for http://jira.pentaho.com/browse/BISERVER-3538
                path = StringUtils.removeStart(path, "/solution");
                InputStream stream = null;

                try {
                    stream = repository.getResourceInputStream(path, FileAccess.EXECUTE, false);
                    cx.evaluateReader(thisObj, new InputStreamReader(stream), path, 1, null);

                } catch (Exception e) {
                    logger.error(e);
                } finally {
                    IOUtils.closeQuietly(stream);
                }
            }
        }

        // Get the paths ot the necessary files: dependencies and the main script.
        return Context.toBoolean(true);
    }

    public void executeScript(String path) {
        Context cx = getContextFactory().enterContext();

        executeScript(cx, path, this);
    }

    public static void executeScript(Context cx, String path, Scriptable scope) {
        cx.setLanguageVersion(Context.VERSION_1_7);
        InputStream stream = null;
        try {
            stream = RepositoryAccess.getRepository().getResourceInputStream(path, FileAccess.EXECUTE, false);
            cx.evaluateReader(scope, new InputStreamReader(stream), path, START_LINE, null);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public static Object print(Context cx, Scriptable thisObj,
            Object[] args, Function funObj) {

        for (Object arg : args) {
            String s = Context.toString(arg);
            logger.info(s);
        }
        return Context.getUndefinedValue();
    }

    public static Object load(Context cx, Scriptable thisObj,
            Object[] args, Function funObj) {
        String file = args[0] instanceof NativeJavaObject ? ((NativeJavaObject) args[0]).unwrap().toString() : args[0].toString();
        executeScript(cx, file, thisObj);
        return Context.toBoolean(true);
    }

    public static Object lib(Context cx, Scriptable thisObj,
            Object[] args, Function funObj) {
        if (args == null || args.length < 1) {
            throw new IllegalArgumentException("lib called with insufficient arguments");
        }
        String file = args[0].toString();
        executeScript(cx, systemPath + file, thisObj);
        return Context.toBoolean(true);
    }

    public static Object callWithDefaultSession(final Context cx, final Scriptable thisObj,
            Object[] args, Function funObj) {
        final Callable callback = (Callable) args[0];
        IPentahoSession old = PentahoSessionHolder.getSession();
        try {
//            HibernateUtil.getSession();
            IPentahoSession session = getAdminSession();
            PentahoSessionHolder.setSession(session);

            callback.call(cx, GlobalScope.getInstance(), thisObj, null);
            HibernateUtil.closeSession();
        } catch (Exception e) {
            logger.error(e);
        } finally {
            PentahoSessionHolder.setSession(old);
        }
        return Context.toBoolean(true);
    }

    public static Object getPluginSetting(Context cx, Scriptable thisObj,
            Object[] args, Function funObj) {
        String path = args[0].toString();
        final IPluginResourceLoader resLoader = PentahoSystem.get(IPluginResourceLoader.class, null);
        String settingValue = resLoader.getPluginSetting(CpkContentGenerator.class, path);
        return Context.toString(settingValue);
    }

    private static IPentahoSession getSession() {
        if (session == null) {
            session = new StandaloneSession("C");
        }
        return session;
    }

    private static IPentahoSession getAdminSession() {
        IUserDetailsRoleListService userDetailsRoleListService = PentahoSystem.getUserDetailsRoleListService();
        UserSession session = new UserSession("admin", null, false, null);
        GrantedAuthority[] auths = userDetailsRoleListService.getUserRoleListService().getAllAuthorities();
        Authentication auth = new AnonymousAuthenticationToken("admin", SecurityHelper.SESSION_PRINCIPAL, auths);
        session.setAttribute(SecurityHelper.SESSION_PRINCIPAL, auth);
        session.doStartupActions(null);
        return session;
    }
}
