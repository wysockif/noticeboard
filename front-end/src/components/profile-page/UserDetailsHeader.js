import React from 'react';
import {Card, Image} from "react-bootstrap";
import defaultProfilePicture from "../../assets/default-profile-image.jpeg";

const UserDetailsHeader = (props) => {
    let profileImage;
    if (props.selectedImage) {
        profileImage = props.selectedImage;
    } else if (props.user && props.user.image) {
        profileImage = "/images/profile/" + props.user.image;
    } else {
        profileImage = defaultProfilePicture;
    }

    return (
        <div>
            <Image src={profileImage}
                   alt="profile-picture"
                   md={4}
                   className="shadow-sm"
                   width="140"
                   height="140"
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

export default UserDetailsHeader;