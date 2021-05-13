import React from 'react';

const PageOptionsSelection = ({onSelectPageSize, onSelectSorting}) => {
    return (
        <div className="col-lg-4 row mt-md-3">
            <div className="col-12  mt-2">
                <label htmlFor="inputPagination" className="form-label">
                    Ilość na stronie
                </label>
                <select id="inputSorting" className="form-select"
                        onChange={onSelectPageSize}>
                    <option value="12" defaultValue>12</option>
                    <option value="24">24</option>
                    <option value="36">36</option>
                </select>
            </div>
            <div className="col-12  mt-2">
                <label htmlFor="inputPagination"
                       className="form-label">Sortowanie</label>
                <select id="inputPagination" className="form-select"
                        onChange={onSelectSorting}>
                    <option value="createdAt,desc" defaultValue>Od najnowszych
                    </option>
                    <option value="createdAt">Od najstarszych</option>
                    <option value="price">Od najtańszych</option>
                    <option value="price,desc">Od najdroższych</option>
                </select>
            </div>
        </div>
    );
};

export default PageOptionsSelection;
