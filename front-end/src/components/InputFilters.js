import React from 'react';
import {FormControl} from "react-bootstrap";

const InputFilters = props => {


    return (
        <div className="col-lg-8 row">
            <div className="col-12 mt-2">
                <label htmlFor="inputLocation" className="form-label">
                    Lokalizacja:
                </label>
                <FormControl
                    type="text"
                    id="inputLocation"
                    placeholder="np. Warszawa"
                    onBlur={props.onBlurLocation}
                    onChange={props.onChangeLocation}
                    value={props.location}
                />
            </div>
            <div className="col-sm-6 mt-2">
                <label htmlFor="inputMinPrice" className="form-label">
                    Cena od:
                </label>
                <FormControl
                    type="text"
                    id="inputMinPrice"
                    placeholder="np. 20 zł"
                    onBlur={props.onBlurMinPrice}
                    onChange={props.onChangeMinPrice}
                    value={props.minPrice}
                    isInvalid={props.minPriceError}
                />
                <FormControl.Feedback type="invalid" className="text-center">
                    {props.minPriceError}
                </FormControl.Feedback>
            </div>
            <div className="col-sm-6 mt-2 mb-md-2">
                <label htmlFor="inputMaxPrice" className="form-label">
                    Cena do:
                </label>
                <FormControl
                    type="text"
                    id="inputMaxPrice"
                    placeholder="np. 100 zł"
                    onBlur={props.onBlurMaxPrice}
                    onChange={props.onChangeMaxPrice}
                    value={props.maxPrice}
                    isInvalid={props.maxPriceError}
                />
                <FormControl.Feedback type="invalid" className="text-center">
                    {props.maxPriceError}
                </FormControl.Feedback>
            </div>
        </div>
    );
}

export default InputFilters;