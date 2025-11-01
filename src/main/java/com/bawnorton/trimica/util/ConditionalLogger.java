package com.bawnorton.trimica.util;

import org.slf4j.Logger;
import org.slf4j.Marker;

import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public final class ConditionalLogger implements Logger {
	private final Logger delegate;
	private final Supplier<Boolean> condition;

	private ConditionalLogger(Logger delegate, Supplier<Boolean> condition) {
		this.delegate = delegate;
		this.condition = condition;
	}

	public static ConditionalLogger of(Logger delegate, Supplier<Boolean> condition) {
		return new ConditionalLogger(delegate, condition);
	}

	private void forward(Runnable runnable) {
		if(condition.get()) runnable.run();
	}

	@Override
	public String getName() {
		return "ConditionalLogger(" + delegate.getName() + ")";
	}

	@Override
	public boolean isTraceEnabled() {
		return condition.get() && delegate.isTraceEnabled();
	}

	@Override
	public void trace(String msg) {
		forward(() -> delegate.trace(msg));
	}

	@Override
	public void trace(String format, Object arg) {
		forward(() -> delegate.trace(format, arg));
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		forward(() -> delegate.trace(format, arg1, arg2));
	}

	@Override
	public void trace(String format, Object... arguments) {
		forward(() -> delegate.trace(format, arguments));
	}

	@Override
	public void trace(String msg, Throwable t) {
		forward(() -> delegate.trace(msg, t));
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return condition.get() && delegate.isTraceEnabled(marker);
	}

	@Override
	public void trace(Marker marker, String msg) {
		forward(() -> delegate.trace(marker, msg));
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		forward(() -> delegate.trace(marker, format, arg));
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		forward(() -> delegate.trace(marker, format, arg1, arg2));
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		forward(() -> delegate.trace(marker, format, argArray));
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		forward(() -> delegate.trace(marker, msg, t));
	}

	@Override
	public boolean isDebugEnabled() {
		return condition.get() && delegate.isDebugEnabled();
	}

	@Override
	public void debug(String msg) {
		forward(() -> delegate.debug(msg));
	}

	@Override
	public void debug(String format, Object arg) {
		forward(() -> delegate.debug(format, arg));
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		forward(() -> delegate.debug(format, arg1, arg2));
	}

	@Override
	public void debug(String format, Object... arguments) {
		forward(() -> delegate.debug(format, arguments));
	}

	@Override
	public void debug(String msg, Throwable t) {
		forward(() -> delegate.debug(msg, t));
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return condition.get() && delegate.isDebugEnabled(marker);
	}

	@Override
	public void debug(Marker marker, String msg) {
		forward(() -> delegate.debug(marker, msg));
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		forward(() -> delegate.debug(marker, format, arg));
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		forward(() -> delegate.debug(marker, format, arg1, arg2));
	}

	@Override
	public void debug(Marker marker, String format, Object... arguments) {
		forward(() -> delegate.debug(marker, format, arguments));
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		forward(() -> delegate.debug(marker, msg, t));
	}

	@Override
	public boolean isInfoEnabled() {
		return condition.get() && delegate.isInfoEnabled();
	}

	@Override
	public void info(String msg) {
		forward(() -> delegate.info(msg));
	}

	@Override
	public void info(String format, Object arg) {
		forward(() -> delegate.info(format, arg));
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		forward(() -> delegate.info(format, arg1, arg2));
	}

	@Override
	public void info(String format, Object... arguments) {
		forward(() -> delegate.info(format, arguments));
	}

	@Override
	public void info(String msg, Throwable t) {
		forward(() -> delegate.info(msg, t));
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return condition.get() && delegate.isInfoEnabled(marker);
	}

	@Override
	public void info(Marker marker, String msg) {
		forward(() -> delegate.info(marker, msg));
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		forward(() -> delegate.info(marker, format, arg));
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		forward(() -> delegate.info(marker, format, arg1, arg2));
	}

	@Override
	public void info(Marker marker, String format, Object... arguments) {
		forward(() -> delegate.info(marker, format, arguments));
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		forward(() -> delegate.info(marker, msg, t));
	}

	@Override
	public boolean isWarnEnabled() {
		return condition.get() && delegate.isWarnEnabled();
	}

	@Override
	public void warn(String msg) {
		forward(() -> delegate.warn(msg));
	}

	@Override
	public void warn(String format, Object arg) {
		forward(() -> delegate.warn(format, arg));
	}
	@Override
	public void warn(String format, Object arg1, Object arg2) {
		forward(() -> delegate.warn(format, arg1, arg2));
	}

	@Override
	public void warn(String format, Object... arguments) {
		forward(() -> delegate.warn(format, arguments));
	}

	@Override
	public void warn(String msg, Throwable t) {
		forward(() -> delegate.warn(msg, t));
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return condition.get() && delegate.isWarnEnabled(marker);
	}

	@Override
	public void warn(Marker marker, String msg) {
		forward(() -> delegate.warn(marker, msg));
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		forward(() -> delegate.warn(marker, format, arg));
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		forward(() -> delegate.warn(marker, format, arg1, arg2));
	}

	@Override
	public void warn(Marker marker, String format, Object... arguments) {
		forward(() -> delegate.warn(marker, format, arguments));
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		forward(() -> delegate.warn(marker, msg, t));
	}

	@Override
	public boolean isErrorEnabled() {
		return condition.get() && delegate.isErrorEnabled();
	}

	@Override
	public void error(String msg) {
		forward(() -> delegate.error(msg));
	}

	@Override
	public void error(String format, Object arg) {
		forward(() -> delegate.error(format, arg));
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		forward(() -> delegate.error(format, arg1, arg2));
	}

	@Override
	public void error(String format, Object... arguments) {
		forward(() -> delegate.error(format, arguments));
	}

	@Override
	public void error(String msg, Throwable t) {
		forward(() -> delegate.error(msg, t));
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return condition.get() && delegate.isErrorEnabled(marker);
	}

	@Override
	public void error(Marker marker, String msg) {
		forward(() -> delegate.error(marker, msg));
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		forward(() -> delegate.error(marker, format, arg));
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		forward(() -> delegate.error(marker, format, arg1, arg2));
	}

	@Override
	public void error(Marker marker, String format, Object... arguments) {
		forward(() -> delegate.error(marker, format, arguments));
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		forward(() -> delegate.error(marker, msg, t));
	}
}
