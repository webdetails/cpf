/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.messaging;

/**
 *
 * @author joao
 */
public interface IEventPublisher {
    public IEventPublisher getPublisher();
    public void publish(final PluginEvent event);
    
}
