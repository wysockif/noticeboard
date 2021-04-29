import React from 'react';
import {Card, FormControl, InputGroup} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

const CreateNoticePageDescription = () => {
    return (
        <Card className="my-3">
            <div className="col-11 mx-auto">
                <Card.Title className="m-3 mb-1"><small>Opis ogłoszenia</small></Card.Title>
                <small className="mx-3 text-muted">Opis ogłoszenia musi zawierać conajmniej 60
                    znaków.</small>
                <Card.Text as="div" className="mx-3 my-2">
                    <InputGroup>
                        <InputGroup.Prepend className="d-none d-sm-block">
                            <InputGroup.Text style={{minWidth: "120px", height: "200px"}}>
                                <FontAwesomeIcon icon="file-alt" className="my-5 me-2"/>
                                Opis:
                            </InputGroup.Text>
                        </InputGroup.Prepend>
                        <FormControl as="textarea" aria-label="Opis:" className="rounded"
                                     style={{resize: "none", height: "200px"}}/>
                    </InputGroup>
                    <div className="mx-3 text-end"><small>0/2000</small></div>
                </Card.Text>
            </div>
        </Card>
    );
};

export default CreateNoticePageDescription;
