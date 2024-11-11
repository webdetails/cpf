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


package pt.webdetails.cpf;

import java.util.concurrent.Callable;

/**
 * Boilerplate to run a method in a different ClassLoader.
 */
public class ClassLoaderAwareCaller {
  private ClassLoader classLoader;

  public ClassLoaderAwareCaller(ClassLoader classLoader){
   this.classLoader = classLoader;
  }

  public <T> T callInClassLoader(Callable<T> callable) throws Exception{
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    try
    {
      if(this.classLoader != null)
      {
        Thread.currentThread().setContextClassLoader(this.classLoader);
      }

      return callable.call();

    }
    finally{
      Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
  }

  public void runInClassLoader(Runnable runnable)
  {
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    try
    {
      if(this.classLoader != null)
      {
        Thread.currentThread().setContextClassLoader(this.classLoader);
      }

      runnable.run();

    }
    finally{
      Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
  }


}
