package net.saifs.odinmc.events.merged;


public interface MergedHandlerMapping<Merged, Priority, Handled> {
    void failed( Merged event,  Throwable error);

    
    Handled map( Object object);

    
    Priority priority();
}
