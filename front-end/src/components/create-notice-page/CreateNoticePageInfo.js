import React from 'react';
import {Card} from "react-bootstrap";
import InputWithValidation from "../InputWithValidation";

const CreateNoticePageInfo = () => {
    return (
            <Card>
                <div className="col-11 mx-auto">
                    <Card.Title className="m-3 mb-1"><small>Podstawowe informacje</small></Card.Title>
                    <Card.Text as="div" className="mx-3 my-2">
                        <InputWithValidation
                            label="Tytuł ogłoszenia:" placeholder="np. Sprzedam rower marki Romet"
                            icon="quote-left" width="170px"
                        />

                        <InputWithValidation
                            label="Lokalizacja:" placeholder="np. Warszawa"
                            icon="map-marker-alt" width="170px"
                        />

                        <InputWithValidation
                            label="Cena:" placeholder="np. 999.99 zł" icon="wallet"
                            width="170px"
                        />
                    </Card.Text>
                </div>
            </Card>
    );
};

export default CreateNoticePageInfo;