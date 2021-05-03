import React, {Component} from 'react';
import {Button, Card, ListGroup} from "react-bootstrap";
import InputWithValidation from "../InputWithValidation";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

class KeywordsInput extends Component {

    state = {
        inputValue: '',
        keywords: ['samochod', 'opel', 'car'],
        errors: undefined,
        info: undefined,
        isMax: false
    };

    onChangeInputValue = event => {
        this.setState({inputValue: event.target.value, errors: undefined});
    }


    onClickAddButton = () => {
        if (this.state.inputValue.length < 3) {
            this.setState({errors: 'Słowa-klucze muszą się składać conajmniej z 3 znaków'});
            return;
        } else if (this.state.inputValue.length > 20) {
            this.setState({errors: 'Słowa-klucze mogą się składać z conajwyżej 20 znaków'});
            return;
        }

        if (!this.state.keywords.includes(this.state.inputValue)) {
            this.setState({keywords: [...this.state.keywords, this.state.inputValue], inputValue: ''});
        } else {
            this.setState({errors: 'Słowa-klucze nie mogą się powtarzać'})
        }

        if (this.state.keywords.length >= 11) {
            this.setState({info: 'Maksymalna liczba słów-kluczy', isMax: true});
        }
    }


    onClickDeleteButton(key) {
        let keywords = [...this.state.keywords].filter(value => value !== key)
        this.setState({keywords, isMax: false, info: undefined});
    }


    render() {
        return (
            <Card className="my-3">
                <div className="col-11 mx-auto mb-3">
                    <Card.Title className="m-3 mb-1"><small>Słowa-klucze</small></Card.Title>
                    <div className="mx-3 ">
                        <small className="text-muted">
                            Słowa-klucze umożliwiają wyszukiwanie ogłoszenia na stronie głównej.
                            Musi być ich conajmniej trzy.
                        </small>
                    </div>
                    <Card.Text as="div" className="mx-3 my-1">
                        <ListGroup className="my-2 justify-content-center row" horizontal>
                            {this.state.keywords.map(key =>
                                <ListGroup.Item key={key} className="col-11 col-sm-6 col-md-3 m-1
                                border-1 rounded d-flex justify-content-between align-items-center">
                                    {key}
                                    <Button className="btn-sm btn-close" aria-label="Close"
                                            onClick={() => this.onClickDeleteButton(key)}
                                    />
                                </ListGroup.Item>
                            )}
                        </ListGroup>
                        {!this.state.isMax &&
                        <div className="mx-auto col-11 col-sm-10 col-md-9 col-lg-7 col-xl-5 text-center mb-1">
                            <InputWithValidation
                                label="Słowo-klucz:"
                                placeholder="np. rower"
                                icon="search"
                                width="140px"
                                value={this.state.inputValue}
                                onChange={this.onChangeInputValue}
                                hasError={this.state.errors}
                                error={this.state.errors}
                            />

                            <Button variant="outline-secondary" onClick={this.onClickAddButton}>
                                <div className="mx-3">
                                    Dodaj<FontAwesomeIcon className="ms-1" icon="plus"/>
                                </div>
                            </Button>
                        </div>}
                        {this.state.isMax && <div className="text-center">
                            <small className="text-muted">{this.state.info}</small>
                        </div>}
                        {this.props.errors && <div className="text-center">
                            <small className="text-danger">{this.props.errors}</small>
                        </div>}
                    </Card.Text>
                </div>
            </Card>
        );
    };
}

export default KeywordsInput;
