package net.odinmc.core.common.util;

import com.google.common.collect.ForwardingSet;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends ForwardingSet<E> {

  private final Set<E> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

  @Override
  protected Set<E> delegate() {
    return set;
  }
}
