// import RegistrationPage from "./pages/RegistrationPage";
// import { register } from './api/apiCalls';
import LoginPage from "./pages/LoginPage";

function App() {
  // const actions = {
  //   postRegister: register
  // };

  return (
    <div className="App">
      <header className="App-header">
        {/* <RegistrationPage actions={actions} /> */}
        <LoginPage/>
      </header>
    </div>
  );
}

export default App;
