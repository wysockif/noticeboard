import React from 'react';
import {Card} from "react-bootstrap";
import SimpleImage from "./SingleImage";

const ImagesUpload = () => {
    return (
        <Card className="mt-2">
            <div className="col-11 mx-auto row">
                <Card.Title className="m-3 mb-1"><small>Zdjęcia</small></Card.Title>
                <small className="mx-3 text-muted">Kolejność zdjęć ma znaczenie.</small>
                <SimpleImage content="Zdjęcie pierwszoplanowe"/>
                <SimpleImage content="Zdjęcie drugoplanowe"/>
                <SimpleImage content="Zdjęcie trzecioplanowe"/>
            </div>
        </Card>
    );
};

export default ImagesUpload;
