package com.orientechnologies.orient.core.index;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.OIndexIsRebuildingException;

import java.util.Map;
import java.util.Set;

public class OIndexChangesWrapper implements OIndexCursor {
  private final OIndex<?>    source;
  private final OIndexCursor delegate;
  private final long         indexVersion;

  public static OIndexCursor wrap(OIndex<?> source, OIndexCursor cursor, long indexVersion) {
    if (cursor instanceof OIndexChangesWrapper)
      return cursor;

    return new OIndexChangesWrapper(source, cursor, indexVersion);
  }

  public OIndexChangesWrapper(OIndex<?> source, OIndexCursor delegate, long indexVersion) {
    this.source = source;
    this.delegate = delegate;

    this.indexVersion = source.getRebuildVersion();
  }

  @Override
  public Map.Entry<Object, OIdentifiable> nextEntry() {
    if (source.isRebuilding())
      throwRebuildException();

    final Map.Entry<Object, OIdentifiable> entry = delegate.nextEntry();

    if (source.getRebuildVersion() != indexVersion)
      throwRebuildException();

    return entry;
  }

  @Override
  public Set<OIdentifiable> toValues() {
    if (source.isRebuilding())
      throwRebuildException();

    final Set<OIdentifiable> values = delegate.toValues();

    if (source.getRebuildVersion() != indexVersion)
      throwRebuildException();

    return values;
  }

  @Override
  public Set<Map.Entry<Object, OIdentifiable>> toEntries() {
    if (source.isRebuilding())
      throwRebuildException();

    final Set<Map.Entry<Object, OIdentifiable>> entries = delegate.toEntries();

    if (source.getRebuildVersion() != indexVersion)
      throwRebuildException();

    return entries;
  }

  @Override
  public Set<Object> toKeys() {
    if (source.isRebuilding())
      throwRebuildException();

    final Set<Object> keys = delegate.toKeys();

    if (source.getRebuildVersion() != indexVersion)
      throwRebuildException();

    return keys;
  }

  @Override
  public void setPrefetchSize(int prefetchSize) {
    delegate.setPrefetchSize(prefetchSize);
  }

  @Override
  public boolean hasNext() {
    if (source.isRebuilding())
      throwRebuildException();

    final boolean isNext = delegate.hasNext();

    if (source.getRebuildVersion() != indexVersion)
      throwRebuildException();

    return isNext;
  }

  @Override
  public OIdentifiable next() {
    if (source.isRebuilding())
      throwRebuildException();

    final OIdentifiable next = delegate.next();

    if (source.getRebuildVersion() != indexVersion)
      throwRebuildException();

    return next;
  }

  private void throwRebuildException() {
    throw new OIndexIsRebuildingException("Index " + source.getName() + " is rebuilding at the moment and can not be used");
  }
}
