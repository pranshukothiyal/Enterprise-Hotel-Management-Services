import { Link } from 'react-router-dom'
import { ArrowLeft, Hotel } from 'lucide-react'
import { Button } from '../components/ui'

export default function NotFoundPage(){return <div className="grid min-h-screen place-items-center bg-slate-950 px-5 text-center text-white"><div><div className="mx-auto grid h-16 w-16 place-items-center rounded-3xl bg-cyan-400 text-slate-950"><Hotel className="h-8 w-8"/></div><p className="mt-8 text-xs font-bold uppercase tracking-[.3em] text-cyan-400">404</p><h1 className="mt-3 font-display text-5xl font-extrabold">Page not found.</h1><p className="mx-auto mt-4 max-w-md text-slate-400">The route you requested does not exist in the StayOps frontend.</p><Link to="/" className="mt-8 inline-block"><Button variant="secondary"><ArrowLeft className="h-4 w-4"/>Back to home</Button></Link></div></div>}
