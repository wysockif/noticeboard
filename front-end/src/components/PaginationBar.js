import React from 'react';

const PaginationBar = ({page, onClickNext, onClickPrevious, onClickFirst, onClickLast}) => {
    const previousClasses = page.first ? 'page-item disabled' : 'page-item';
    const nextClasses = page.last ?  'page-item disabled' : 'page-item';

    return (
        <nav className="my-2">
            <ul className="pagination justify-content-center">
                <li className={previousClasses} onClick={onClickPrevious}><span
                    className="page-link text-dark">{'<'}</span></li>
                {page.number > 1 &&
                <li className="page-item" onClick={onClickFirst}><span className="page-link text-dark">
                                    1
                                </span></li>}
                {page.number > 2 &&
                <li className="page-item disabled"><span className="page-link text-dark">
                                    ...
                                </span></li>}
                {!page.first &&
                <li className="page-item">
                                <span
                                    className="page-link text-dark"
                                    onClick={onClickPrevious}>
                                {page.number}
                            </span>
                </li>}
                <li className="page-item active">
                                <span className="page-link text-dark">
                                    {page.number + 1}
                                </span>
                </li>
                {!page.last &&
                <li className="page-item" onClick={onClickNext}>
                                <span className="page-link text-dark">
                                    {page.number + 2}
                                </span>
                </li>}
                {page.totalPages - page.number > 3 &&
                <li className="page-item disabled"><span className="page-link text-dark">
                                    ...
                                </span></li>}
                {page.totalPages - page.number > 2 &&
                <li className="page-item" onClick={onClickLast}><span className="page-link text-dark">
                                {page.totalPages}
                                </span></li>}
                <li className={nextClasses} onClick={onClickNext}><span
                    className="page-link text-dark">{'>'}</span></li>
            </ul>
        </nav>
    );
};

export default PaginationBar;
