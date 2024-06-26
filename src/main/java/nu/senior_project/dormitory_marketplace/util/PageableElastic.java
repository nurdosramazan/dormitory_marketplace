package nu.senior_project.dormitory_marketplace.util;

import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class PageableElastic implements Pageable {

    private final int offset;
    private final int page;
    private final int size;
    private final Sort sort = Sort.unsorted();

    protected PageableElastic(int offset, int page, int size) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be less than zero!");
        }

        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }

        if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one!");
        }

        this.offset = offset;
        this.page = page;
        this.size = size;
    }

    public static PageableElastic of(int offset, int page, int size) {
        return new PageableElastic(offset, page, size);
    }

    @Override
    public boolean isPaged() {
        return Pageable.super.isPaged();
    }

    @Override
    public boolean isUnpaged() {
        return Pageable.super.isUnpaged();
    }

    @Override
    public int getPageNumber() {
        return page;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return offset + (long) page * size;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Sort getSortOr(Sort sort) {
        return Pageable.super.getSortOr(sort);
    }

    public Pageable next() {
        return of(offset, page + 1, size);
    }

    public Pageable previousOrFirst() {
        return hasPrevious() ? of(offset, page - 1, size) : first();
    }

    public Pageable first() {
        return of(offset, 0, size);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return of(offset, pageNumber, size);
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    @Override
    public Optional<Pageable> toOptional() {
        return Pageable.super.toOptional();
    }

    @Override
    public OffsetScrollPosition toScrollPosition() {
        return Pageable.super.toScrollPosition();
    }
}