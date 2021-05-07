import React from 'react';
import {Card} from "react-bootstrap";
import InputWithValidation from "../InputWithValidation";

const ContactInformation = ({email}) => {
    return (
        <Card className="my-3">
            <div className="col-11 mx-auto">
                <Card.Title className="m-3 mb-1"><small>Dane kontaktowe</small></Card.Title>
                <Card.Text as="div" className="mx-3 my-2">
                    <InputWithValidation
                        label="Adres email:" value={email} icon="at" width="170px" readOnly={true}
                    />
                </Card.Text>
            </div>
        </Card>
    );
};


export default ContactInformation;
