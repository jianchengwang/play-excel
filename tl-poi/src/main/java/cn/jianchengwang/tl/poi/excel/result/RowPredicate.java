package cn.jianchengwang.tl.poi.excel.result;

/**
 * Excel Row Predicate
 *
 */
@FunctionalInterface
public interface RowPredicate<R, T> {

    Valid test(R r, T t);

}