import React from 'react';
import {Card} from "react-bootstrap";
import InputWithValidation from "../InputWithValidation";

const BasicInformation = props => {
    return (
        <Card>
            <div className="col-11 mx-auto">
                <Card.Title className="m-3 mb-1"><small>Podstawowe informacje</small></Card.Title>
                <Card.Text as="div" className="mx-3 my-2">
                    <InputWithValidation
                        label="Tytuł ogłoszenia:"
                        placeholder="np. Sprzedam rower"
                        icon="quote-left"
                        width="170px"
                        value={props.title}
                        onChange={props.onChangeTitle}
                        hasError={props.titleError && true}
                        error={props.titleError}
                    />
                    <InputWithValidation
                        label="Lokalizacja:"
                        placeholder="np. Warszawa"
                        icon="map-marker-alt"
                        width="170px"
                        value={props.location}
                        onChange={props.onChangeLocation}
                        hasError={props.locationError && true}
                        error={props.locationError}
                    />
                    <InputWithValidation
                        label="Cena:"
                        placeholder="np. 999.99 zł"
                        icon="wallet"
                        width="170px"
                        value={props.price}
                        onChange={props.onChangePrice}
                        hasError={props.priceError && true}
                        error={props.priceError}
                    />
                    {props.priceError &&
                    <div className="text-muted text-center">
                        <small>
                            Akceptowane formaty ceny:
                            "1000", "1000zł", "1000.00", "1000.00zł", "1000 zł", "1000.00 zł".
                        </small>
                    </div>
                    }
                </Card.Text>
            </div>
        </Card>
    );
};

export default BasicInformation;