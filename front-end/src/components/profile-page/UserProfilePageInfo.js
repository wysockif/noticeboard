import React from 'react';
import {Card, Image} from "react-bootstrap";
import defaultProfilePicture from "../../assets/default-profile-image.jpeg";

const UserProfilePageInfo = (props) => {
    let profileImage = defaultProfilePicture;
    if (props.user && props.user.image) {
        profileImage = props.user.image;
    }

    return (
        <div>
            <Image src={profileImage} alt="profile-picture"
                   md={4}
                   className="shadow-sm"
                   width="100"
                   height="100"
                   roundedCircle
                   onError={event => event.target.src = defaultProfilePicture}
            />
            <Card.Title
                className="mt-1">{props.user.firstName} {props.user.lastName}</Card.Title>
            <Card.Subtitle
                className="text-muted">{`@${props.user.username}`}</Card.Subtitle>
        </div>
    );
};

export default UserProfilePageInfo;