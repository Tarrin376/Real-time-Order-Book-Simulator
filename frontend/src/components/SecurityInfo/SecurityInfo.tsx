import { Socket } from "socket.io-client";
import { securities, Security } from "../../types/Security";

interface SecurityInfoProps {
    socket: Socket | undefined,
    security: Security,
    changeSecurity: (security: Security) => void
}

function SecurityInfo({ socket, security, changeSecurity }: SecurityInfoProps) {
    return (
        <div className="component">
            <div className="security-close-info">
                <div className="close-price-wrapper">
                    <p className="buy-price close-price">265.34</p>
                    <select className="security-dropdown" onChange={(e) => changeSecurity(e.target.value)}>
                        {securities.map(security => {
                            return (
                                <option key={security}>
                                    {security}
                                </option>
                            )
                        })}
                    </select>
                </div>
                <p className="side-text">
                    As of today at 21:28 UTC+2
                </p>
            </div>
        </div>
    )
}

export default SecurityInfo;