import { Socket } from "socket.io-client";
import { securities, Security } from "../../types/Security";
import { useEffect, useState } from "react";
import { OHLC } from "../../types/OHLC";
import { formatTimestampToTime } from "../../utils/dateFormats";
import Arrow from "../Icons/CloseArrow";
import InfoSection from "./InfoSection";

interface SecurityInfoProps {
    socket: Socket | undefined,
    security: Security,
    changeSecurity: (security: Security) => void
}

function SecurityInfo({ socket, security, changeSecurity }: SecurityInfoProps) {
    const [ohlc, setOHLC] = useState<OHLC>();

    function handleOHLCEvent(ohlc: OHLC) {
        setOHLC(ohlc);
    }

    function determineCloseState(): number {
        if (!ohlc?.close) {
            return 0;
        }

        return ohlc.close >= ohlc.open ? 1 : -1;
    }

    useEffect(() => {
        if (!socket) {
            return;
        }

        socket.on(`ohlc-${security}`, handleOHLCEvent);
        return () => {
            socket.off(`ohlc-${security}`, handleOHLCEvent);
        }
    }, [socket, security]);

    return (
        <div className="component security-info">
            <div className="security-close-info">
                <div className="close-price-wrapper">
                    <div className="close-arrow-wrapper">
                        <p className={`close-price ${determineCloseState() == 1 ? 'buy-price' : determineCloseState() == -1 ? 'sell-price' : ''}`}>
                            {ohlc?.close.toFixed(2)}
                        </p>
                        <Arrow state={determineCloseState()} />
                    </div>
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
                    {`As of today at ${ohlc?.timestamp ? formatTimestampToTime(ohlc.timestamp) : "00:00"} UTC`}
                </p>
            </div>
            <div className="info-section-wrapper">
                <InfoSection 
                    title="Spread" 
                    value={"9.32"} 
                />
                <InfoSection 
                    title="Best Bid" 
                    value={"246.13"} 
                />
                <InfoSection 
                    title="Best Ask" 
                    value={"250.13"} 
                />
                <InfoSection 
                    title="Liquidity Ratio" 
                    value={"1.12"} 
                />
                <InfoSection 
                    title="24H Price Volatility" 
                    value={"3.56%"} 
                />
                <InfoSection 
                    title="24H High / Low" 
                    value={"241.77 / 246.13"} 
                />
                <InfoSection 
                    title="24H Volume" 
                    value={"335K / 804M"} 
                />
            </div>
        </div>
    )
}

export default SecurityInfo;