import React, {Component} from 'react';
import {Card, FormControl, InputGroup} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

export class DescriptionForm extends Component {
    state = {
        length: 0
    }

    onChangeDescription = event => {
        const newLength = event.target.value.length;
        this.setState({length: newLength})
        this.props.onChangeDescription(event);
    }

    render() {
        return (
            <Card className="my-3">
                <div className="col-11 mx-auto">
                    <Card.Title className="m-3 mb-1">
                        <small>Opis ogłoszenia</small>
                    </Card.Title>
                    <div className="mx-3">
                        <small className="text-muted">
                            Opis ogłoszenia musi zawierać conajmniej 60 znaków.
                        </small>
                    </div>
                    <Card.Text as="div" className="mx-3 my-2">
                        <InputGroup>
                            <InputGroup.Prepend className="d-none d-sm-block">
                                <InputGroup.Text style={{minWidth: "120px", height: "200px"}}>
                                    <FontAwesomeIcon icon="file-alt" className="my-5 me-2"/>
                                    Opis:
                                </InputGroup.Text>
                            </InputGroup.Prepend>
                            <FormControl as="textarea" aria-label="Opis:" className="rounded"
                                         style={{resize: "none", height: "200px"}}
                                         value={this.props.description} onChange={this.onChangeDescription}
                            />
                        </InputGroup>
                        <div className="mx-3 text-end"><small>{`${this.state.length}/2000`}</small></div>
                    </Card.Text>
                </div>
            </Card>
        );
    };
}

export default DescriptionForm;
