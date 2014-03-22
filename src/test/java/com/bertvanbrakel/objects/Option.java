package com.bertvanbrakel.objects;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

public abstract class Option<T> {

	private static final None<?> NONE = new None<Object>();

	private Option() {
	}

	public final <E> Option<E> foreach(Callable<E> call) throws Exception {
		if (isPresent()) {
			return Option.fromNullable(call.call());
		} else {
			return none();
		}
	}

	public final <E> Option<E> foreach(Function<T, E> func) {
		if (isPresent()) {
			return Option.fromNullable(func.apply(get()));
		} else {
			return none();
		}
	}

	public final void foreach(Runnable runnable) {
		if (isPresent()) {
			runnable.run();
		}
	}
	
	public Iterable<T> toIterable(){
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return toIterator();
			}
		};
	}

	public abstract Iterator<T> toIterator();

	public final boolean isPresent() {
		return !isAbsent();
	}

	public final T getOr(Supplier<? extends T> supplier) {
		checkNotNull(supplier, "expect supplier");
		if (isPresent()) {
			return get();
		} else {
			return supplier.get();
		}
	}

	public final T getOr(Callable<T> callable) throws Exception {
		if (isPresent()) {
			return get();
		} else {
			return callable.call();
		}
	}
	
	public final T getOr(Option<? extends T> option) {
		checkNotNull(option, "expect option");
		if (isPresent()) {
			return get();
		} else {
			return option.get();
		}
	}

	public final T getOr(T defaultVal) {
		if (isPresent()) {
			return get();
		} else {
			return defaultVal;
		}
	}

	public final T getOrNull(){
		if(isPresent()){
			return get();
		} else {
			return null;
		}
	}
	
	public abstract boolean isAbsent();

	public abstract T get() throws IllegalStateException;

	public static <T> Option<T> fromNullable(T val) {
		return val == null ? Option.<T>none() : new Some<T>(val);
	}

	@SuppressWarnings("unchecked")
	public static <T> Option<T> none() {
		return (Option<T>) NONE;
	}

	public static <T> Option<T> some(T val) {
		return new Some<T>(val);
	}

	public static final class None<T> extends Option<T> {

		private static final NoneIterator<Object> NONE_ITER = new NoneIterator<Object>();
		
		private None(){
		}
		
		@Override
		public T get() throws IllegalStateException {
			throw new IllegalStateException("no value");
		}

		@Override
		public boolean isAbsent() {
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Iterator<T> toIterator() {
			return (Iterator<T>) NONE_ITER;
		}
	}
	
	public static final class Some<T> extends Option<T> {
		private final T val;

		private Some(T val) {
			this.val = checkNotNull(val);
		}

		@Override
		public T get() {
			return val;
		}

		@Override
		public boolean isAbsent() {
			return false;
		}

		@Override
		public Iterator<T> toIterator() {
			return new SomeIterator<T>(val);
		}
	}
	
	private static final class SomeIterator<T> implements Iterator<T> {
		private final T val;
		private boolean nextCalled = false;

		
		public SomeIterator(T val) {
			super();
			this.val = val;
		}

		@Override
		public boolean hasNext() {
			return !nextCalled;
		}

		@Override
		public T next() {
			if( nextCalled){
				throw new NoSuchElementException();
			}
			nextCalled = true;
			return val;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("'remove' is not supported");
		}
	}
	
	private static final class NoneIterator<T> implements Iterator<T>{

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public T next() {
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("'remove' is not supported");
		}
	}
}
