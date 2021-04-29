import React, {Component} from 'react';
import {Card, Container} from 'react-bootstrap';
import {connect} from 'react-redux';
import {Redirect} from 'react-router';
import ButtonWithSpinner from '../components/ButtonWithSpinner';
import AdvertiserInfo from '../components/create-notice-page/AdvertiserInfo';
import CreateNoticePageContact from '../components/create-notice-page/ContactInformation';
import DescriptionForm from '../components/create-notice-page/DescriptionForm';
import ImagesUpload from '../components/create-notice-page/ImagesUpload';
import KeywordsInput from '../components/create-notice-page/KeywordsInput';

class CreateNoticePage extends Component {
    keywordsComponent = React.createRef();

    state = {}

    onClickSubmit = () => {
        console.log(this.keywordsComponent.current.state.keywords)
    }

    render() {
        return (
            <Container data-testid="createnoticepage" className="my-3">
                {!this.props.isLoggedIn && <Redirect to="/login"/>}
                <Card>
                    <Card.Header className="text-center">
                        <h5 className="my-2">Dodaj ogłoszenie </h5>
                    </Card.Header>
                    <Card.Body>
                        <AdvertiserInfo/>
                        <CreateNoticePageContact email={this.props.email}/>
                        <DescriptionForm/>
                        <KeywordsInput ref={this.keywordsComponent}/>
                        <ImagesUpload/>
                        <div className="text-center my-2">
                            <ButtonWithSpinner content="Dodaj ogłoszenie" onClick={this.onClickSubmit}/>
                        </div>
                    </Card.Body>
                </Card>
            </Container>
        );
    }
}

const mapStateToProps = state => {
    return {
        isLoggedIn: state.isLoggedIn,
        email: state.email
    }
}

export default connect(mapStateToProps)(CreateNoticePage);