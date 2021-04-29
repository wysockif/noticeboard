import React, {Component} from 'react';
import {Button, Card, ListGroup} from "react-bootstrap";
import InputWithValidation from "../InputWithValidation";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

class CreateNoticePageKeywords extends Component {

    state = {
        inputValue: '',
        keywords: []
    };

    onChangeInputValue = event => {
        this.setState({inputValue: event.target.value});
    }

    onClickAddButton = () => {
        this.setState({keywords: [...this.state.keywords, this.state.inputValue], inputValue: ''});
    }


    onClickDeleteButton(key) {
        let keywords = [...this.state.keywords].filter(value => value !== key)
        this.setState({keywords});
    }

    render() {
        return (
            <Card className="my-3">
                <div className="col-11 mx-auto">
                    <Card.Title className="m-3 mb-1"><small>Słowa-klucze</small></Card.Title>
                    <small className="mx-3 text-muted">
                        Słowa-klucze umożliwiają wyszukiwanie ogłoszenia na stronie głównej.
                        Musi być ich conajmniej trzy.
                    </small>
                    <Card.Text as="div" className="mx-3 my-1">
                        <ListGroup className="my-2 justify-content-center row" horizontal>
                            {this.state.keywords.map(key =>
                                <ListGroup.Item key={key} className="col-11 col-sm-6 col-md-3 m-1 border-1 rounded">
                                    <Button className="btn-sm btn-close me-2" aria-label="Close"
                                            onClick={() => this.onClickDeleteButton(key)}
                                    />
                                    {key}
                                </ListGroup.Item>
                            )}
                        </ListGroup>
                        <div className="mx-auto col-11 col-sm-10 col-md-9 col-lg-7 col-xl-5 text-center">
                            <InputWithValidation
                                label="Słowo-klucz:"
                                placeholder="np. rower"
                                icon="search"
                                width="140px"
                                value={this.state.inputValue}
                                onChange={this.onChangeInputValue}
                            />
                            <Button variant="outline-secondary" className="mb-3" onClick={this.onClickAddButton}>
                                <div className="mx-3">Dodaj <FontAwesomeIcon className="ms-1" icon="plus"/>
                                </div>
                            </Button>
                        </div>
                    </Card.Text>
                </div>
            </Card>
        );
    };
}

export default CreateNoticePageKeywords;
