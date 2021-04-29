import React, {Component} from 'react';
import {Card, Container} from "react-bootstrap";
import ButtonWithSpinner from "../components/ButtonWithSpinner";
import CreateNoticePageInfo from '../components/create-notice-page/CreateNoticePageInfo';
import CreateNoticePageContact from "../components/create-notice-page/CreateNoticePageContact";
import CreateNoticePageDescription from "../components/create-notice-page/CreateNoticePageDescription";
import CreateNoticePageKeywords from "../components/create-notice-page/CreateNoticePageKeywords";
import CreateNoticePageImages from "../components/create-notice-page/CreateNoticePageImages";

class CreateNoticePage extends Component {
    render() {
        return (
            <Container data-testid="createnoticepage" className="my-3">
                <Card>
                    <Card.Header className="text-center">
                        <h5 className="my-2">Dodaj ogłoszenie </h5>
                    </Card.Header>
                    <Card.Body>
                        <CreateNoticePageInfo/>
                        <CreateNoticePageContact/>
                        <CreateNoticePageDescription/>
                        <CreateNoticePageKeywords/>
                        <CreateNoticePageImages/>
                        <div className="text-center my-2">
                            <ButtonWithSpinner content="Dodaj ogłoszenie"/>
                        </div>
                    </Card.Body>
                </Card>
            </Container>
        );
    }
}

export default CreateNoticePage;