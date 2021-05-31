import React from 'react';
import {Button, Card} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import EditFormCollapse from "./EditFormCollapse";
import UserDetailsHeader from "./UserDetailsHeader";

const UserPageHeader = props => {
    return (
        <Card.Header className="text-center">
            <div className="col-10 col-sm-9 col-md-8 mx-auto">
                {props.user &&
                <UserDetailsHeader user={props.user} selectedImage={props.selectedImage}/>}
                {props.canBeModified && <div className="text-center mt-2">
                    <Button
                        data-testid="collapse-button"
                        size="sm"
                        onClick={props.onClickCollapseButton}
                        aria-controls="example-collapse-text"
                        aria-expanded={props.open}
                        variant="light"
                    >
                        {!props.open && <div className="text-muted">
                            <small>Pokaż panel edycji</small>
                            <FontAwesomeIcon icon="arrow-down" className="mx-1"/>
                        </div>}
                        {props.open && <div className="text-muted">
                            <small>Anuluj zmiany i ukryj panel edycji</small>
                            <FontAwesomeIcon icon="arrow-up" className="mx-1"/>
                        </div>}

                    </Button>
                </div>}
                <EditFormCollapse
                    ongoingApiCall={props.ongoingApiCall}
                    open={props.open}
                    user={props.user}
                    onClickUpdateUser={props.onClickUpdateUser}
                    errors={props.errors}
                    onImageSelect={props.onImageSelect}
                />
            </div>
        </Card.Header>
    );
}

export default UserPageHeader;