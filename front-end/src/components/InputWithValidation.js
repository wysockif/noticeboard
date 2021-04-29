import React from 'react';
import {FormControl, InputGroup} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

const InputWithValidation = props => {
    const type = props.type ? props.type : 'text';
    const labelWidth = props.width ? props.width : '200px';
    const style = props.readOnly ? {backgroundColor: "white"} : {};
    return (
        <div>
            <label htmlFor={props.value} className="d-block d-sm-none mb-1">{props.label}</label>
            <InputGroup className="mb-3">
                <InputGroup.Prepend style={{minWidth: labelWidth}} className="d-none d-sm-block">
                    <InputGroup.Text>
                        {props.icon && <FontAwesomeIcon icon={props.icon} className="my-2 me-2"/>}
                        {props.label}
                    </InputGroup.Text>
                </InputGroup.Prepend>
                <FormControl
                    style={style}
                    className={"rounded-1"}
                    id={props.value}
                    placeholder={props.placeholder}
                    value={props.value}
                    onChange={props.onChange}
                    type={type}
                    isInvalid={props.hasError}
                    isValid={props.isCorrect && true}
                    readOnly={props.readOnly}
                />
                <FormControl.Feedback type="invalid" className="text-center">
                    {props.hasError && props.error}
                </FormControl.Feedback>
            </InputGroup>
        </div>
    );
}

InputWithValidation.defaultProps = {
    onChange: () => {
    }
};

export default InputWithValidation;